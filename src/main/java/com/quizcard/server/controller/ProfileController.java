package com.quizcard.server.controller;

import com.quizcard.server.dto.QuizDto;
import com.quizcard.server.security.CustomUserDetails;
import com.quizcard.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String getProfile(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        model.addAttribute("user", userDetails.getUser().getUsername());
        List<QuizDto> quizDtoList = userService.getAllUserQuiz(userDetails.getUser());
        model.addAttribute("quizList", quizDtoList);
        return "profile";
    }

    @PostMapping("/delete/{quizId}")
    public String deleteQuiz(@AuthenticationPrincipal
                             CustomUserDetails userDetails,
                             Model model,
                             @PathVariable
                             String quizId) {

        userService.deleteQuiz(userDetails.getUser(), quizId);

        return "profile";
    }

    @PostMapping("/edit/{quizId}")
    public String editQuizProtect(@AuthenticationPrincipal
                                  CustomUserDetails userDetails,
                                  Model model,
                                  @PathVariable
                                  String quizId) {

        userService.editQuiz(userDetails.getUser(), quizId);

        return "profile";
    }

}
