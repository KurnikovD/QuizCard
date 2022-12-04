package com.quizcard.server.controller;

import com.quizcard.server.dto.UserDto;
import com.quizcard.server.security.CustomUserDetails;
import com.quizcard.server.service.UserService;
import com.quizcard.server.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/signUp")
public class SingUpController {

    private final UserService userService;

    @GetMapping
    public String singUp(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {

        if (userDetails != null) {
            return "redirect:/";
        }
        model.addAttribute("userForm", new UserDto());

        return "signUp";
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
