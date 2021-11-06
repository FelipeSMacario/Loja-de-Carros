package com.JavangularCar.LojadeCarro.service;

import com.JavangularCar.LojadeCarro.model.Cores;
import com.JavangularCar.LojadeCarro.repository.CoresRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
public class CoresService {
    @Autowired
    CoresRepository coresRepository;

    public Cores createCores(@RequestBody Cores cores){
        return coresRepository.save(cores);
    }

    public List<Cores> listarCores(){
        return coresRepository.findAll();
    }
    public ResponseEntity<Cores> findCoresById(Long id){
        return coresRepository.findById(id)
                                .map(record -> ResponseEntity.ok().body(record))
                                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
    public ResponseEntity updateCores(@RequestBody Cores cores, Long id){
        return coresRepository.findById(id)
                                .map(record -> {
                                    record.setNome(cores.getNome());
                                    Cores update = coresRepository.save(record);
                                    return ResponseEntity.ok().body(update);
                                }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
    public ResponseEntity deleteCores(Long id){
        return coresRepository.findById(id)
                                .map(record -> {
                                    coresRepository.deleteById(id);
                                    return ResponseEntity.ok().build();
                                }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

}
