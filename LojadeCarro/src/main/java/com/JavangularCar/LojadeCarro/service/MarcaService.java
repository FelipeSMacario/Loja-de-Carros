package com.JavangularCar.LojadeCarro.service;

import com.JavangularCar.LojadeCarro.model.Marca;
import com.JavangularCar.LojadeCarro.repository.MarcaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
public class MarcaService {
    @Autowired
    MarcaRepository marcaRepository;

    public Marca createMarca(Marca marca) {
        return marcaRepository.save(marca);
    }

    public List<Marca> listarMarcas() {
        return marcaRepository.findByOrderByNomeAsc();
    }

    public ResponseEntity<Marca> findMarcaById(Long id) {
        return marcaRepository.findById(id)
                .map(record -> ResponseEntity.ok().body(record))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    public ResponseEntity updateMarca(@RequestBody Marca marca, Long id) {
        return marcaRepository.findById(id)
                .map(record -> {
                    record.setNome(marca.getNome());
                    record.setUrl(marca.getUrl());
                    Marca update = marcaRepository.save(record);
                    return ResponseEntity.ok().body(update);
                }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    public ResponseEntity deleteMarca(Long id) {
        return marcaRepository.findById(id)
                .map(record -> {
                    marcaRepository.deleteById(id);
                    return ResponseEntity.ok().build();
                }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}
