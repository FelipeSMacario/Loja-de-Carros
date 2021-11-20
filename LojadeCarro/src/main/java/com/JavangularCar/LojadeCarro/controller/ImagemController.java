package com.JavangularCar.LojadeCarro.controller;

import com.JavangularCar.LojadeCarro.service.ImagemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("imagem")
public class ImagemController {

    @Autowired
    ImagemService imagemService;

    @PostMapping
    public void upload(@RequestParam("file") MultipartFile[] multipartFile) throws IOException {
        imagemService.updateArquivos(multipartFile);
    }
}
