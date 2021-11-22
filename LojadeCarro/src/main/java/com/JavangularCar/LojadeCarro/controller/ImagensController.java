package com.JavangularCar.LojadeCarro.controller;


import com.JavangularCar.LojadeCarro.model.Imagens;
import com.JavangularCar.LojadeCarro.service.ImagensService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("imagens")
public class ImagensController {
    @Autowired
    ImagensService imagensService;

    @PostMapping
    public void createImagens(@RequestParam("file") MultipartFile[] file, @RequestParam("id")String id) throws IOException {
        for (int c = 0; c < file.length; c ++){
            Imagens imagens = new Imagens();

            //Converter a do carro enviada pelo FORMDATA do angular ID de String para LONG
            Long id2 = Long.parseLong(id);

            imagensService.createImagem(imagens, file[c], id2);

            //Função que insere o id do carro após a criação da imagem
            imagensService.updateEstoque(id2, imagens.getId());

            //Função que filtra a primeira imagem inserida e define como imagem Default no carro
            if (c == 0) {
                imagensService.imagemCarroDefault(imagens.getUrl(), id2);
            }
        }
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
