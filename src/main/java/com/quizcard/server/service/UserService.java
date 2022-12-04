package com.quizcard.server.service;

import com.quizcard.server.dto.QuizDto;
import com.quizcard.server.dto.UserDto;
import com.quizcard.server.entity.User;

import java.util.List;

public interface UserService {

        void signUp(UserDto userDto);

        boolean signIn(UserDto userDto);

        List<QuizDto> getAllUserQuiz(User user);

        void deleteQuiz(User user, String quizId);

        void editQuiz(User user, String quizId);
}
