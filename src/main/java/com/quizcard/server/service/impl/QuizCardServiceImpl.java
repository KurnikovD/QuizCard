package com.quizcard.server.service.impl;

import com.quizcard.server.dto.*;
import com.quizcard.server.entity.Question;
import com.quizcard.server.entity.Quiz;
import com.quizcard.server.entity.Topic;
import com.quizcard.server.entity.User;
import com.quizcard.server.repository.QuestionRepository;
import com.quizcard.server.repository.QuizRepository;
import com.quizcard.server.repository.RoundRepository;
import com.quizcard.server.repository.TopicRepository;
import com.quizcard.server.service.QuizCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
//@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class QuizCardServiceImpl implements QuizCardService {

    private final QuizRepository quizRepository;
    private final RoundRepository roundRepository;
    private final TopicRepository topicRepository;
    private final QuestionRepository questionRepository;

    @Value("${QuizCard.path.media}")
    private String media;

    private QuizDto quizDto;
    private List<RoundDto> roundDtos;
    private String currentRoundId;
    private List<DeskDto> currentDesk;

    public List<QuizDto> getAllQuiz() {
        return quizRepository.findByIsProtect(false).stream()
                .map(QuizDto::new)
                .collect(Collectors.toList());
    }

    public void start(String quizId) {
        Quiz quizEntity = quizRepository.findById(quizId).orElse(null);
        if (quizEntity == null) {
            throw new IllegalArgumentException("Wrong id");
        }

        quizDto = new QuizDto(quizEntity.getId(),
                quizEntity.getTitle(),
                roundRepository.findByQuiz_Id(quizId).size(),
                quizEntity.isProtect());
        roundDtos = roundRepository.findByQuiz_Id(quizId).stream()
                .map(RoundDto::toRoundDto)
                .collect(Collectors.toList());
        roundDtos.forEach(roundDto ->
                roundDto.setDesk(topicRepository
                        .findByRound_Id(roundDto.getId())
                        .stream()
                        .map(this::toDeskDto)
                        .collect(Collectors.toList())));
    }

    private DeskDto toDeskDto(Topic topic) {
        List<CostDto> costs = questionRepository.findByTopic_IdOrderByCostAsc(topic.getId()).stream()
                .map(this::toCostDto)
                .collect(Collectors.toList());
        return new DeskDto(topic.getId(), topic.getName(), costs);
    }

    private CostDto toCostDto(Question question) {
        return new CostDto(
                question.getId(),
                question.getCost(),
                question.isCat(),
                false
        );
    }

    public void createDesk(String roundId) {
        currentRoundId = roundId;
        currentDesk = roundDtos.stream().filter(it -> it.getId().equals(roundId)).findFirst().orElseThrow().getDesk();
    }

    public List<RoundDto> getRounds() {
        return roundDtos;
    }

    public boolean checkRoundsEnd() {
        if (currentDesk.stream().allMatch(t -> t.getCosts().stream().allMatch(CostDto::isPassed))) {
            roundDtos.stream()
                    .filter(it -> it.getId().equals(currentRoundId))
                    .findFirst()
                    .orElseThrow()
                    .setPassed(true);
            return true;
        }
        return false;
    }

    public List<DeskDto> getDesk() {
        return currentDesk;
    }

    public QuestionDto getQuestion(String questionId) {

        for (DeskDto deskDto : currentDesk) {
            for (CostDto costDto : deskDto.getCosts()) {
                if (costDto.getId().equals(questionId)) {
                    costDto.setPassed(true);
                    Question question = questionRepository.findById(questionId).orElseThrow();
                    return toQuestionDto(question);
                }
            }
        }

        throw new IllegalArgumentException("Cannot find question");

    }

    private QuestionDto toQuestionDto(Question it) {
        return new QuestionDto(it, media);
    }

    public AnswerDto getAnswer(String questionId) {
        return questionRepository.findById(questionId).map(this::doAnswerDto).orElseThrow();
    }

    private AnswerDto doAnswerDto(Question it) {
        return new AnswerDto(it, media);
    }

    public CatDto getCat(String questionId) {
        return questionRepository.findById(questionId).map(this::toCatDto).orElseThrow();
    }

    private CatDto toCatDto(Question it) {
        return new CatDto(it, media);
    }

    public List<Quiz> getQuizByName(String name) {
        return quizRepository.findByTitleContainsIgnoreCase(name);
    }


    public List<QuizDto> getAllUsersAndPublicQuiz(User user) {
        return quizRepository.findByUser_IdOrIsProtect(user.getId(), false).stream()
                .map(QuizDto::new)
                .collect(Collectors.toList());
    }
}
