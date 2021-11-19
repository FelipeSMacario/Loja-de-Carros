package com.JavangularCar.LojadeCarro.service;

import com.JavangularCar.LojadeCarro.model.Imagens;
import com.JavangularCar.LojadeCarro.repository.ImagensRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
public class ImagensService {
    @Autowired
    ImagensRepository imagensRepository;

    public Imagens createImagem(@RequestBody Imagens imagens) {
        return imagensRepository.save(imagens);
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
    public ResponseEntity deleteImagens(Long id){
        return imagensRepository.findById(id)
                                            .map(record -> {
                                                imagensRepository.deleteById(id);
                                                return ResponseEntity.ok().build();
                                            }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}
