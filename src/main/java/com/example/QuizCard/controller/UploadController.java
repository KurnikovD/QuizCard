package com.example.QuizCard.controller;

import com.example.QuizCard.service.LoadService;
import net.bytebuddy.build.AccessControllerPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Access;

@Controller
public class UploadController {

//    private String

    @Autowired
    LoadService loadService;

    @GetMapping("/load")
    public String load(){
        return "load";
    }

    @PostMapping("/packLoad")
    public String packLoad(@RequestParam("file")MultipartFile file){

        loadService.savePack(file);
        loadService.addData();
        return "";
    }

}


