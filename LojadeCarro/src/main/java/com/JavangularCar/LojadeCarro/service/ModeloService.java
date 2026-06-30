package com.JavangularCar.LojadeCarro.service;

import com.JavangularCar.LojadeCarro.entity.Modelo;
import com.JavangularCar.LojadeCarro.repository.MoleloRepository;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModeloService {
    @Autowired
    MoleloRepository moleloRepository;

    public Modelo createModelo(Modelo modelo) {
        return moleloRepository.save(modelo);
    }

    public List<Modelo> listarModelo() {
        return moleloRepository.findAll();
    }

    public Modelo findModeloById(Long id) {
        return moleloRepository.findById(id)
                .stream().findFirst()
                .orElseThrow(() -> new ServiceException("Modelo não encontrado"));
    }

    public ResponseEntity updateModelo(Modelo modelo, Long id) {
        return moleloRepository.findById(id)
                .map(record -> {
                    record.setMarca(modelo.getMarca());
                    record.setNome(modelo.getNome());
                    Modelo update = moleloRepository.save(record);
                    return ResponseEntity.ok().body(update);
                }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    public ResponseEntity deleteModelo(Long id) {
        return moleloRepository.findById(id)
                .map(record -> {
                    moleloRepository.deleteById(id);
                    return ResponseEntity.ok().build();
                }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());

    }

}
