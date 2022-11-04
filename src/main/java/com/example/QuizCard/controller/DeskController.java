package com.example.QuizCard.controller;

import com.example.QuizCard.dto.QuestionDto;
import com.example.QuizCard.service.QuizCardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
//@SessionScope
public class DeskController {

    private final QuizCardService quizCardService;

    public DeskController(QuizCardService quizCardService) {
        this.quizCardService = quizCardService;
    }

    @GetMapping("/start")
    public String start(@RequestParam String quizId) {
        quizCardService.start(quizId);
        return "redirect:/round";
    }

    @GetMapping("/answer")
    public String answer(Model model) {

        if (quizCardService.getQuizDto() == null) {
            return "redirect:/";
        }

        QuestionDto question = quizCardService.getCurrentQuestion();
        model.addAttribute("question", question);
        return "answer";
    }

    @GetMapping("/round")
    public String chooseRound(Model model) {
        model.addAttribute("listOfRounds", quizCardService.getRounds());
        return "round";
    }

    @GetMapping("/nextRound")
    public String nextRound(@RequestParam String roundNumber) {
        quizCardService.createDesk(roundNumber);
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
    public String getQuestion(@RequestParam("topicId") String topicId,
                              @RequestParam("questionId") String questionId,
                              Model model) {

        QuestionDto question = quizCardService.getQuestion(topicId, questionId);

        model.addAttribute("question", question);

        return "question";
    }

}
