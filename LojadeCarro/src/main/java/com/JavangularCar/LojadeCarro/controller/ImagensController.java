package com.JavangularCar.LojadeCarro.controller;

import com.JavangularCar.LojadeCarro.model.Imagens;
import com.JavangularCar.LojadeCarro.service.ImagensService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("imagens")
public class ImagensController {
    @Autowired
    ImagensService imagensService;

    @PostMapping
    public Imagens createImagens(@RequestBody Imagens imagens){
        return imagensService.createImagem(imagens);
    }

    @GetMapping
    public List<Imagens> listarImagens(){
        return imagensService.listarImagens();
    }
    @GetMapping("/{id}")
    public ResponseEntity<Imagens> findImagemById(@PathVariable Long id){
        return imagensService.findImagensById(id);
    }
    @PutMapping("/{id}")
    public ResponseEntity updateImagem(@RequestBody Imagens imagens, @PathVariable Long id){
        return imagensService.updateImagens(imagens, id);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity deleteImagem(@PathVariable Long id){
        return imagensService.deleteImagens(id);
    }
}
