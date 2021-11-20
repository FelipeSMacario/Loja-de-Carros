package com.JavangularCar.LojadeCarro.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class ImagemService {
    public void updateArquivos(MultipartFile[] multipartFile) throws IOException {

        for (MultipartFile file : multipartFile) {
            file.transferTo(new File("D:\\TesteArquivoJava\\" + file.getOriginalFilename()));
        }




       // multipartFile.transferTo(new File("D:\\TesteArquivoJava\\" + multipartFile.getOriginalFilename()));


    }
}
