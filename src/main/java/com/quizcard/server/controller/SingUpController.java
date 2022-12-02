package com.quizcard.server.controller;

import com.quizcard.server.dto.UserDto;
import com.quizcard.server.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/singUp")
public class SingUpController {

    private final UserService userService;

    public SingUpController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String singUp(Authentication authentication, Model model) {

        if (authentication != null) {
            return "redirect:/";
        }
        model.addAttribute("userForm", new UserDto());

        return "singUp";
    }

    @PostMapping
    public String singUp(UserDto userDto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("userForm", userDto);
            return "signUp";
        }
        userService.signUp(userDto);

        return "redirect:/load";
    }
}
