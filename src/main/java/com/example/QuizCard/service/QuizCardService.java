package com.example.QuizCard.service;

import com.example.QuizCard.dto.QuestionDto;
import com.example.QuizCard.dto.QuizDto;
import com.example.QuizCard.dto.RoundDto;
import com.example.QuizCard.dto.TopicDto;
import com.example.QuizCard.entity.Question;
import com.example.QuizCard.entity.Quiz;
import com.example.QuizCard.entity.Round;
import com.example.QuizCard.entity.Topic;
import com.example.QuizCard.repository.QuestionRepository;
import com.example.QuizCard.repository.QuizRepository;
import com.example.QuizCard.repository.RoundRepository;
import com.example.QuizCard.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
//@Scope(value = WebApplicationContext.SCOPE_SESSION,
//        proxyMode = ScopedProxyMode.TARGET_CLASS)
public class QuizCardService {

    final QuizRepository quizRepository;
    final RoundRepository roundRepository;
    final TopicRepository topicRepository;
    final QuestionRepository questionRepository;

    @Value("${QuizCard.path.media}")
    private String media;

    private QuizDto quizDto;
    private List<RoundDto> roundDtos;
    private List<TopicDto> desk;
    private QuestionDto currentQuestion;

    public QuizCardService(QuizRepository quizRepository,
                           RoundRepository roundRepository,
                           TopicRepository topicRepository,
                           QuestionRepository questionRepository) {
        this.quizRepository = quizRepository;
        this.roundRepository = roundRepository;
        this.topicRepository = topicRepository;
        this.questionRepository = questionRepository;
    }

    public List<QuizDto> getAllQuiz() {
        return quizRepository.findAll().stream().map(it -> new QuizDto(
                        it.getId(),
                        it.getTitle(),
                        roundRepository.findByQuiz_Id(it.getId()).size()))
                .toList();
    }

    public void start(String quizId) {
        Quiz quizEntity = quizRepository.findById(quizId).orElse(null);
        if (quizEntity == null) {
            throw new IllegalArgumentException("Wrong id");
        }

        quizDto = new QuizDto(quizEntity.getId(), quizEntity.getTitle(), roundRepository.findByQuiz_Id(quizId).size());
        roundDtos = roundRepository.findByQuiz_Id(quizId).stream().map(this::toRoundDto).toList();
    }

    private RoundDto toRoundDto(Round round) {
        return new RoundDto(round.getId(), round.getName(), round.getRoundNumber(), round.getPassed());
    }

    private TopicDto toTopicDto(Topic topic) {
        return new TopicDto(topic.getId(),
                topic.getName(),
                topic.getQuestions()
                        .stream()
                        .map(this::toQuestionDto)
                        .sorted(Comparator.comparing(QuestionDto::getCost))
                        .toList(),
                topic.getPassed());
    }

    private QuestionDto toQuestionDto(Question it) {
        String mediaPath = switch (it.getMediaType()) {
            case IMAGE, VIDEO, AUDIO -> media + quizDto.getId() + "/" + it.getId() + it.getMediaExtensionPath();
            default -> "";
        };
        return new QuestionDto(it.getId(), it.getQuestion(), it.getAnswer(), it.getCost(), it.getMediaType(), mediaPath, it.getPassed());
    }

    public void createDesk(String roundId) {
        desk = topicRepository.findByRound_Id(roundId).stream().map(this::toTopicDto).toList();
    }

    public List<RoundDto> getRounds() {
        return roundDtos;
    }

    public boolean checkRoundsEnd() {
        return desk.stream().allMatch(t -> t.getQuestions().stream().allMatch(QuestionDto::getPassed));
    }

    public List<TopicDto> getDesk() {
        return desk;
    }

    public QuestionDto getQuestion(String topicId, String questionId) {

        for (TopicDto topicDto : desk) {
            if (topicDto.getId().equals(topicId)) {
                for (int j = 0; j < topicDto.getQuestions().size(); j++) {
                    if (topicDto.getQuestions().get(j).getId().equals(questionId)) {
                        topicDto.getQuestions().get(j).setPassed(true);
                        return currentQuestion = topicDto.getQuestions().get(j);
                    }
                }
            }
        }

        throw new IllegalArgumentException("Cannot find question");

    }

    public QuestionDto getCurrentQuestion() {
        if (currentQuestion == null) {
            throw new IllegalArgumentException("No eny question yet");
        }
        return currentQuestion;
    }

    public QuizDto getQuizDto() {
        return quizDto;
    }

    public List<Quiz> getQuizByName(String name) {
        return quizRepository.findByTitleContainsIgnoreCase(name);
    }
}
