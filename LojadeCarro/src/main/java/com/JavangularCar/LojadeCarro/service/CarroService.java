package com.JavangularCar.LojadeCarro.service;

import com.JavangularCar.LojadeCarro.model.Carro;
import com.JavangularCar.LojadeCarro.model.Marca;
import com.JavangularCar.LojadeCarro.repository.CarroRepository;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;


@Service

public class CarroService {
    @Autowired
    CarroRepository carroRepository;

    public Carro createCarro(Carro carro) {
        return carroRepository.save(carro);
    }

    public Page<Carro> listarCarros(Pageable pageable) {
        return carroRepository.findAll(pageable);
    }

    public ResponseEntity<Carro> findCarroById(Long id) {
        return carroRepository.findById(id)
                .map(record -> ResponseEntity.ok().body(record))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());

    }

    public ResponseEntity updateCarro(@RequestBody Carro carro, Long id) {
        return carroRepository.findById(id)
                .map(record -> {
                    record.setQuilometragem(carro.getQuilometragem());
                    record.setAtivo(carro.isAtivo());
                    record.setUrl(carro.getUrl());
                    record.setValor(carro.getValor());
                    record.setPlaca(carro.getPlaca());
                    record.setMotor(carro.getMotor());
                    record.setAnoFabricacao(carro.getAnoFabricacao());
                    record.setDtCadastro(carro.getDtCadastro());
                    record.setCarroceria(carro.getCarroceria());
                    record.setMarca(carro.getMarca());
                    record.setCores(carro.getCores());
                    record.setModelo(carro.getModelo());
                    record.setUsuario(carro.getUsuario());
                    record.setCombustivel(carro.getCombustivel());
                    Carro update = carroRepository.save(record);
                    return ResponseEntity.ok().body(update);
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    public ResponseEntity deleteCarro(Long id) {
        return carroRepository.findById(id)
                .map(record -> {
                    carroRepository.deleteById(id);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());

    }

    public Page<Carro> FiltrarCampos(String marca, String modelo, Integer anoInicio, Integer anoFim, Double valorInicio, Double valorFim, Double quilometragem, Pageable pageable) {
        return carroRepository.FindByCampos(marca, modelo, anoInicio, anoFim, valorInicio, valorFim, quilometragem, pageable);
    }


    public ResponseEntity macarVendido(@RequestBody Carro carro, Long id) {
        return carroRepository.findById(id).map(
                record -> {
                    record.setQuilometragem(carro.getQuilometragem());
                    record.setAtivo(false);
                    record.setUrl(carro.getUrl());
                    record.setValor(carro.getValor());
                    record.setPlaca(carro.getPlaca());
                    record.setMotor(carro.getMotor());
                    record.setAnoFabricacao(carro.getAnoFabricacao());
                    record.setDtCadastro(carro.getDtCadastro());
                    record.setCarroceria(carro.getCarroceria());
                    record.setMarca(carro.getMarca());
                    record.setCores(carro.getCores());
                    record.setModelo(carro.getModelo());
                    record.setUsuario(carro.getUsuario());
                    record.setCombustivel(carro.getCombustivel());
                    Carro update = carroRepository.save(record);
                    return ResponseEntity.ok().body(update);
                }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}
