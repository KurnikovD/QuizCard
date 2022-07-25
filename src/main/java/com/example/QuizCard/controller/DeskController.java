package com.example.QuizCard.controller;

import com.example.QuizCard.entity.Question;
import com.example.QuizCard.service.QuizCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.annotation.SessionScope;


@Controller
@SessionScope
public class DeskController {

    @Autowired
    private QuizCardService quizCardService;

    @GetMapping("/start")
    public String start(@RequestParam Long quizId) {
        quizCardService.start(quizId);
        return "redirect:/round";
    }

    @GetMapping("/answer")
    public String answer(Model model) {

        if (quizCardService.getQuiz() == null){
            return "redirect:/";
        }

        Question question = quizCardService.getCurrentQuestion();
        model.addAttribute("question", question);
        return "answer";
    }

    @GetMapping("/round")
    public String chooseRound(Model model) {
        model.addAttribute("listOfRounds", quizCardService.getRounds());
        return "round";
    }

    @GetMapping("/nextRound")
    public String nextRound(@RequestParam Integer roundNumber) {
        quizCardService.setCurrentRound(roundNumber);
        quizCardService.setDesk();
        return "redirect:/desk";
    }

    @GetMapping("/desk")
    public String getDesk(Model model) {

        if (quizCardService.checkRoundsEnd()) {
            return "redirect:/round";
        }

        model.addAttribute("desk", quizCardService.getDesk());
        return "desk";
    }

    @GetMapping("/question")
    public String getQuestion(@RequestParam Integer topicNumber,
                              @RequestParam Integer questionNumber,
                              Model model) {

        Question question = quizCardService.getQuestion(topicNumber, questionNumber);

        model.addAttribute("question", question);

        return "question";
    }

}
