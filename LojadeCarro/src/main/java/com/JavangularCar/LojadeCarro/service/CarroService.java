package com.JavangularCar.LojadeCarro.service;

import com.JavangularCar.LojadeCarro.model.Carro;
import com.JavangularCar.LojadeCarro.model.Marca;
import com.JavangularCar.LojadeCarro.repository.CarroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

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
                    record.setPlaca(carro.getPlaca());
                    record.setMotor(carro.getMotor());
                    record.setMotor(carro.getMotor());
                    record.setCarroceria(carro.getCarroceria());
                    record.setMarca(carro.getMarca());
                    record.setCores(carro.getCores());
                    record.setModelo(carro.getModelo());
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

    public List<Carro> FiltrarCampos(String marca, String modelo, Integer anoInicio, Integer anoFim, Double valorInicio, Double valorFim, Double quilometragem){
        return carroRepository.FindByCampos(marca, modelo, anoInicio, anoFim, valorInicio, valorFim, quilometragem);
    }

//    public List<Carro> findByValorBetween(Double valor1, Double valor2, int ano1, int ano2){
//        return carroRepository.findByValorBetweenAndAnoFabricacaoBetween(valor1, valor2, ano1, ano2);
//    }

}
