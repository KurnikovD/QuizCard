package com.quizcard.server.service.impl;

import com.quizcard.server.dto.QuizDto;
import com.quizcard.server.dto.UserDto;
import com.quizcard.server.entity.Quiz;
import com.quizcard.server.entity.User;
import com.quizcard.server.repository.QuizRepository;
import com.quizcard.server.repository.UserRepository;
import com.quizcard.server.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final QuizRepository quizRepository;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(QuizRepository quizRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.quizRepository = quizRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void signUp(UserDto userDto) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        userRepository.save(user);
    }

    @Override
    public boolean signIn(UserDto userDto) {
        User user = userRepository.findByUsername(userDto.getUsername());
        return user != null && user.getPassword().equals(userDto.getPassword());
    }

    @Override
    public List<QuizDto> getAllUserQuiz(User user) {

        return quizRepository.findAllByUser_Id(user.getId()).stream().map(this::toQuizDto).collect(Collectors.toList());

    }

    private QuizDto toQuizDto(Quiz quiz) {
        return new QuizDto(quiz.getId(), quiz.getTitle(), quiz.getRounds().size(), quiz.isProtect());
    }

    @Override
    public void deleteQuiz(User user, String quizId) {
        // TODO: maybe late
    }

    @Override
    public void editQuiz(User user, String quizId) {
        Quiz quiz = quizRepository.findByIdAndUser_Id(quizId, user.getId());
        if (quiz == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        quiz.setProtect(!quiz.isProtect());
        quizRepository.save(quiz);
    }
}
