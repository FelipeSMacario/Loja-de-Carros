package com.JavangularCar.LojadeCarro.service;

import com.JavangularCar.LojadeCarro.model.Compras;
import com.JavangularCar.LojadeCarro.repository.ComprasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
public class ComprasService {
    @Autowired
    ComprasRepository comprasRepository;

    public Compras createCompras(@RequestBody Compras compras){
        return comprasRepository.save(compras);
    }

    public List<Compras> listarCompras() {
        return comprasRepository.findAll();
    }
}
