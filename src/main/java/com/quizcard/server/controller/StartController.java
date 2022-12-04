package com.quizcard.server.controller;

import com.quizcard.server.dto.QuizDto;
import com.quizcard.server.entity.Quiz;
import com.quizcard.server.security.CustomUserDetails;
import com.quizcard.server.service.QuizCardService;
import com.quizcard.server.service.impl.QuizCardServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class StartController {

    final QuizCardService quizCardService;

//    @GetMapping("/")
//    public String index(Model model) {
//        List<QuizDto> listOfQuiz = quizCardService.getAllQuiz();
//        model.addAttribute("listOfQuiz", listOfQuiz);
//
//        return "index";
//    }

    @GetMapping("/")
    public String indexUser(@AuthenticationPrincipal
                            CustomUserDetails userDetails,
                            Model model) {
        if (userDetails != null) {
            List<QuizDto> listOfQuiz = quizCardService.getAllUsersAndPublicQuiz(userDetails.getUser());
            model.addAttribute("listOfQuiz", listOfQuiz);
        } else {
            List<QuizDto> listOfQuiz = quizCardService.getAllQuiz();
            model.addAttribute("listOfQuiz", listOfQuiz);
        }
//        List<QuizDto> listOfQuiz = quizCardService.getAllUsersAndPublicQuiz(userDetails.getUser());
//        model.addAttribute("listOfQuiz", listOfQuiz);

        return "index";
    }

    @PostMapping("/")
    public String search(String name, Model model) {
        List<Quiz> listOfQuiz = quizCardService.getQuizByName(name);
        model.addAttribute("listOfQuiz", listOfQuiz);
        return "index";
    }
}
