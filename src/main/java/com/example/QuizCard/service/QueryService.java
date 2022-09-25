package com.example.QuizCard.service;

import com.example.QuizCard.entity.Question;
import com.example.QuizCard.entity.Quiz;
import com.example.QuizCard.entity.Round;
import com.example.QuizCard.entity.Topic;
import com.example.QuizCard.repository.QuestionRepository;
import com.example.QuizCard.repository.QuizRepository;
import com.example.QuizCard.repository.RoundRepository;
import com.example.QuizCard.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class QueryService {

    @Autowired
    QuizRepository quizRepository;
    @Autowired
    RoundRepository roundRepository;
    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    TopicRepository topicRepository;

    public Quiz getQuizById(Long quizId) {
        return quizRepository.findById(quizId).orElse(null);
    }

    public List<Round> getListOfRounds(Long id) {
        return roundRepository.findById_Quiz_Id(id);
    }

    public Round getRoundById(Long quizId, Integer roundNumber) {

        return roundRepository.findById_Quiz_IdAndId_RoundNumber(quizId, roundNumber);
    }

    public Map<Topic, List<Question>> getDesk(Long quizId, Integer roundNumber) {
        Map<Topic, List<Question>> desk = new HashMap<>();

        Quiz quiz = quizRepository.findById(quizId).orElse(null);
        Round round = roundRepository.findById_Quiz_IdAndId_RoundNumber(quizId, roundNumber);

        for (Topic topic : topicRepository.findById_RoundId_QuizAndId_RoundId_RoundNumber(quiz, roundNumber)) {
            List<Question> questionList = questionRepository.findById_TopicId_RoundIdAndId_TopicId_TopicNumber(round.getId(), topic.getId().getTopicNumber());
            desk.put(topic, questionList);
        }

        return desk;
    }

    public List<Quiz> findAllQuiz() {
        return quizRepository.findAll();
    }

    public List<Quiz> getQuizByName(String name) {
        return quizRepository.findByTitleContainsIgnoreCase(name);
    }

    public Quiz quizSave(Quiz quiz){
        return quizRepository.save(quiz);
    }
}
