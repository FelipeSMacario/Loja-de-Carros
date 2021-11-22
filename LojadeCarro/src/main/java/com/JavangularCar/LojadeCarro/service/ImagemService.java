package com.JavangularCar.LojadeCarro.service;

import com.JavangularCar.LojadeCarro.model.Imagem;
import com.JavangularCar.LojadeCarro.repository.ImagemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;


@Service
public class ImagemService {
    @Autowired
    ImagemRepository imagemRepository;

    public Imagem updateArquivos(MultipartFile[] multipartFile, Imagem imagem) throws IOException {

        String cliente = "Felipe";

        new File("D:\\TesteArquivoJava\\" + cliente).mkdirs();

        for (MultipartFile file : multipartFile) {

        file.transferTo(new File("D:\\TesteArquivoJava\\" + cliente + "\\" + file.getOriginalFilename()));

        imagem.setUrl("D:/TesteArquivoJava/" + cliente + "/" + file.getOriginalFilename());
        }

        return imagemRepository.save(imagem);
    }
}
