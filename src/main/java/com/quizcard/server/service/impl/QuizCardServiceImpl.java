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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
//@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class QuizCardServiceImpl implements QuizCardService {

    final QuizRepository quizRepository;
    final RoundRepository roundRepository;
    final TopicRepository topicRepository;
    final QuestionRepository questionRepository;

    @Value("${QuizCard.path.media}")
    private String media;

    private QuizDto quizDto;
    private List<RoundDto> roundDtos;
    private String currentRoundId;
    private List<DeskDto> currentDesk;

    public QuizCardServiceImpl(QuizRepository quizRepository,
                               RoundRepository roundRepository,
                               TopicRepository topicRepository,
                               QuestionRepository questionRepository) {
        this.quizRepository = quizRepository;
        this.roundRepository = roundRepository;
        this.topicRepository = topicRepository;
        this.questionRepository = questionRepository;
    }

    public List<QuizDto> getAllQuiz() {
        return quizRepository.findByIsProtect(false).stream().map(it -> new QuizDto(
                        it.getId(),
                        it.getTitle(),
                        roundRepository.findByQuiz_Id(it.getId()).size(),
                        it.isProtect()))
                .toList();
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
        roundDtos = roundRepository.findByQuiz_Id(quizId).stream().map(RoundDto::toRoundDto).toList();
        roundDtos.forEach(roundDto ->
                roundDto.setDesk(topicRepository
                        .findByRound_Id(roundDto.getId())
                        .stream()
                        .map(this::toDeskDto)
                        .toList()));
    }

    private DeskDto toDeskDto(Topic topic) {
        return new DeskDto(topic.getId(),
                topic.getName(),
                questionRepository.findByTopic_IdOrderByCostAsc(topic.getId()).stream().map(this::toCostDto).toList());
    }

    private CostDto toCostDto(Question question) {
        return new CostDto(question.getId(),
                question.getCost(),
                question.isCat(),
                false);
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
            roundDtos.stream().filter(it -> it.getId().equals(currentRoundId)).findFirst().orElseThrow().setPassed(true);
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
                    return toQuestionDto(questionRepository.findById(questionId).orElseThrow());
                }
            }
        }

        throw new IllegalArgumentException("Cannot find question");

    }

    private QuestionDto toQuestionDto(Question it) {
        String mediaPath = switch (it.getQuestionMediaType()) {
            case IMAGE, VIDEO, AUDIO -> media + quizDto.getId() + "/" + it.getId() + it.getQuestionMediaPath();
            default -> "";
        };
        return new QuestionDto(it.getId(),
                it.getQuestion(),
                it.getQuestionMediaType().toString(),
                mediaPath,
                it.getCost());
    }

    public AnswerDto getAnswer(String questionId) {
        return questionRepository.findById(questionId).map(this::doAnswerDto).orElseThrow();
    }

    private AnswerDto doAnswerDto(Question it) {
        String mediaPath = switch (it.getAnswerMediaType()) {
            case IMAGE, VIDEO, AUDIO -> media + quizDto.getId() + "/" + it.getId() + it.getAnswerMediaPath();
            default -> "";
        };
        return new AnswerDto(it.getId(),
                it.getAnswer(),
                it.getAnswerMediaType().toString(),
                mediaPath);
    }

    public CatDto getCat(String questionId) {
        return questionRepository.findById(questionId).map(this::toCatDto).orElseThrow();
    }

    private CatDto toCatDto(Question it) {
        String mediaPath = switch (it.getCatMediaPath()) {
            case "cat.jpg" -> media + "packs/cat.jpg";
            default -> media + quizDto.getId() + "/" + it.getId() + it.getCatMediaPath();
        };

        return new CatDto(it.getId(),
                mediaPath);
    }

    public List<Quiz> getQuizByName(String name) {
        return quizRepository.findByTitleContainsIgnoreCase(name);
    }


    public List<QuizDto> getAllUsersAndPublicQuiz(User user) {
        return quizRepository.findByUser_IdOrIsProtect(user.getId(), false).stream().map(it -> new QuizDto(
                it.getId(),
                it.getTitle(),
                roundRepository.findByQuiz_Id(it.getId()).size(),
                it.isProtect())
        ).toList();
    }
}
