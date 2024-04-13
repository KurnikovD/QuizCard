package com.quizcard.server.controller;

import com.quizcard.server.exception.LoadDataException;
import com.quizcard.server.service.LoadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
public class UploadController {

    final LoadService loadService;

    @GetMapping("/load")
    public String load() {
        return "load";
    }

    @PostMapping("/packLoad")
    public String packLoad(@RequestParam("file") MultipartFile file) {

        loadService.savePack(file);
        loadService.addData();
        loadService.removeTempDir();
        return "redirect:/";
    }

    @ExceptionHandler(LoadDataException.class)
    public String handleLoadDataException(LoadDataException e) {
        return e.getMessage();
    }


}
