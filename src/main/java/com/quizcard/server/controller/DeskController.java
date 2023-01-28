package com.quizcard.server.controller;

import com.quizcard.server.dto.QuestionDto;
import com.quizcard.server.service.QuizCardService;
import com.quizcard.server.service.impl.QuizCardServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequiredArgsConstructor
//@SessionScope
public class DeskController {

    private final QuizCardService quizCardService;

    @GetMapping("/start")
    public String start(@RequestParam String quizId) {
        quizCardService.start(quizId);
        return "redirect:/round";
    }

    @GetMapping("/answer")
    public String answer(@RequestParam("questionId") String questionId, Model model) {
        model.addAttribute("answer", quizCardService.getAnswer(questionId));
        return "answer";
    }

    @GetMapping("/round")
    public String chooseRound(Model model) {
        model.addAttribute("listOfRounds", quizCardService.getRounds());
        return "round";
    }

    @GetMapping("/nextRound")
    public String nextRound(@RequestParam("roundId") String roundId) {
        quizCardService.createDesk(roundId);
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
    public String getQuestion(@RequestParam("questionId") String questionId, Model model) {

        QuestionDto question = quizCardService.getQuestion(questionId);

        model.addAttribute("question", question);

        return "question";
    }

    @GetMapping("/cat")
    public String getCat(@RequestParam("questionId") String questionId,
                              Model model) {

        model.addAttribute("cat", quizCardService.getCat(questionId));

        return "cat";
    }

}
