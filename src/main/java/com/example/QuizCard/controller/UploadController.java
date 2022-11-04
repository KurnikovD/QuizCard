package com.example.QuizCard.controller;

import com.example.QuizCard.exception.LoadDataException;
import com.example.QuizCard.service.LoadService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class UploadController {

    final LoadService loadService;

    public UploadController(LoadService loadService) {
        this.loadService = loadService;
    }

    @GetMapping("/load")
    public String load() {
        return "load";
    }

    @PostMapping("/packLoad")
    public String packLoad(@RequestParam("file") MultipartFile file) {

        loadService.savePack(file);
        loadService.addData();
        return "redirect:/";
    }

    @ExceptionHandler(LoadDataException.class)
    public void handleLoadDataException(LoadDataException e) {
        System.out.println(e.getExceptionLog());
    }

}
