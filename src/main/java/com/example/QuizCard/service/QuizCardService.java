package com.example.QuizCard.service;

import com.example.QuizCard.entity.Question;
import com.example.QuizCard.entity.Quiz;
import com.example.QuizCard.entity.Round;
import com.example.QuizCard.entity.Topic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Map;

@Service
@Scope(value = WebApplicationContext.SCOPE_SESSION,
        proxyMode = ScopedProxyMode.TARGET_CLASS)
public class QuizCardService {

    @Autowired
    QueryService queryService;
    private Map<Topic, List<Question>> desk;
    private Quiz quiz;
    private List<Round> rounds;
    private Round currentRound;
    private Question currentQuestion;

    public List<Quiz> getAllQuiz() {
        return queryService.findAllQuiz();
    }

    public void start(Long quizId) {
        quiz = queryService.getQuizById(quizId);
        rounds = queryService.getListOfRounds(quizId);
    }

    public void setCurrentRound(Integer roundNumber) {
        currentRound = queryService.getRoundById(quiz.getId(), roundNumber);
    }

    public void setDesk() {
        desk = queryService.getDesk(quiz.getId(), currentRound.getId().getRoundNumber());
    }


    public List<Round> getRounds() {
        return rounds;
    }

    public boolean checkRoundsEnd() {
        return currentRound.getCountOfTopics().equals(currentRound.getCountOfTopicsPass());
    }

    public Map<Topic, List<Question>> getDesk() {
        return desk;
    }

    public Question getQuestion(Integer topicNumber, Integer questionNumber) {
        for (Topic topic : desk.keySet()) {
            if (topic.getId().getTopicNumber().equals(topicNumber)) {
                currentQuestion = desk.get(topic).get(questionNumber - 1);
                currentQuestion.setPassed(true);
                topic = topicsUpdate(topic);
                desk.get(topic).set(questionNumber - 1, currentQuestion);

                break;
            }
        }

        return currentQuestion;
    }

    private Topic topicsUpdate(Topic topic) {
        topic.setCountOfQuestionPass(topic.getCountOfQuestionPass() + 1);
        if (topic.getCountOfQuestionPass().equals(topic.getCountOfQuestion())) {
            topic.setPassed(true);
            currentRound.setCountOfTopicsPass(currentRound.getCountOfTopicsPass() + 1);
        }
        return topic;
    }

    public Question getCurrentQuestion() {
        return currentQuestion;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public List<Quiz> getQuizByName(String name) {
        return queryService.getQuizByName(name);
    }
}
