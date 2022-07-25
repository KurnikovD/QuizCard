package com.example.QuizCard.controller;

import com.example.QuizCard.entity.Quiz;
import com.example.QuizCard.service.QuizCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class StartController {

    @Autowired
    QuizCardService quizCardService;

    @GetMapping("/")
    public String index(Model model) {
        List<Quiz> listOfQuiz = quizCardService.getAllQuiz();
        model.addAttribute("listOfQuiz", listOfQuiz);

        return "index";
    }
    @PostMapping("/")
    public String search(String name, Model model){
        List<Quiz> listOfQuiz = quizCardService.getQuizByName(name);
        model.addAttribute("listOfQuiz", listOfQuiz);
        return "index";
    }
}
