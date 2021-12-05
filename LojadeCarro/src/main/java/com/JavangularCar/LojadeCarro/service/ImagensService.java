package com.JavangularCar.LojadeCarro.service;

import ch.qos.logback.core.joran.util.StringToObjectConverter;
import com.JavangularCar.LojadeCarro.model.Carro;
import com.JavangularCar.LojadeCarro.model.Imagens;
import com.JavangularCar.LojadeCarro.repository.ImagensRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public class ImagensService {
    @Autowired
    ImagensRepository imagensRepository;

    public void createImagem(Imagens imagens, MultipartFile files, Long id2) throws IOException {
        String cliente = id2.toString();
        new File("D:\\Loja de carro\\LojaDeCarroAngular\\src\\assets\\TesteArquivoJava\\" + cliente).mkdirs();
        files.transferTo(new File("D:\\Loja de carro\\LojaDeCarroAngular\\src\\assets\\TesteArquivoJava\\" + cliente + "\\" + files.getOriginalFilename()));

        imagens.setUrl("../../../assets/TesteArquivoJava/" + cliente + "/" + files.getOriginalFilename());

        imagensRepository.save(imagens);

    }

    public List<Imagens> listarImagens() {
        return imagensRepository.findAll();
    }

    public ResponseEntity<Imagens> findImagensById(Long id) {
        return imagensRepository.findById(id)
                .map(record ->
                        ResponseEntity.ok().body(record)
                ).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    public ResponseEntity updateImagens(@RequestBody Imagens imagens, Long id) {
        return imagensRepository.findById(id)
                .map(record -> {
                    record.setCarro(imagens.getCarro());
                    record.setUrl(imagens.getUrl());
                    Imagens update = imagensRepository.save(record);
                    return ResponseEntity.ok().body(update);
                }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    public ResponseEntity deleteImagens(Long id) {
        return imagensRepository.findById(id)
                .map(record -> {
                    imagensRepository.deleteById(id);
                    return ResponseEntity.ok().build();
                }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    public void updateEstoque(Long idCarro, Long idImagem) {
        imagensRepository.updateEstoque(idCarro, idImagem);
    }

    public void imagemCarroDefault(String urlDefault, Long idCarro) {
        imagensRepository.imagemCarroDefault(urlDefault, idCarro);
    }
}
