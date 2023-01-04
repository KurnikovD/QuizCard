package com.quizcard.server.service;

import com.quizcard.server.dto.*;
import com.quizcard.server.entity.Question;
import com.quizcard.server.entity.Quiz;
import com.quizcard.server.entity.Topic;
import com.quizcard.server.entity.User;

import java.util.List;

public interface QuizCardService {
    void start(String quizId);
    void createDesk(String roundId);
    List<RoundDto> getRounds();
    boolean checkRoundsEnd();
    List<DeskDto> getDesk();
    QuestionDto getQuestion(String questionId);
    AnswerDto getAnswer(String questionId);
    CatDto getCat(String questionId);
    List<Quiz> getQuizByName(String name);
    List<QuizDto> getAllQuiz();
    List<QuizDto> getAllUsersAndPublicQuiz(User user);

}
