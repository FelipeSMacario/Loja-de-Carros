package com.JavangularCar.LojadeCarro.controller;


import com.JavangularCar.LojadeCarro.model.Imagem;
import com.JavangularCar.LojadeCarro.service.ImagemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("imagem")
@CrossOrigin(origins = "http://localhost:4200")
public class ImagemController {

    @Autowired
    ImagemService imagemService;

    @PostMapping
    public Imagem upload(@RequestParam("file") MultipartFile[] multipartFile, Imagem imagem) throws IOException {
        return  imagemService.updateArquivos(multipartFile, imagem);
    }
}
