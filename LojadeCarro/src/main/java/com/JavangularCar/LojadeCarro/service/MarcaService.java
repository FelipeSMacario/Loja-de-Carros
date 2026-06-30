package com.JavangularCar.LojadeCarro.service;

import com.JavangularCar.LojadeCarro.dto.request.MarcaRequest;
import com.JavangularCar.LojadeCarro.dto.response.MarcaResponse;
import com.JavangularCar.LojadeCarro.entity.Marca;
import com.JavangularCar.LojadeCarro.mapper.MarcaMapper;
import com.JavangularCar.LojadeCarro.repository.MarcaRepository;
import org.hibernate.service.spi.ServiceException;
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

    @Autowired
    private MarcaMapper marcaMapper;

    public MarcaResponse createMarca(MarcaRequest marcaDTO) {
        var marca = marcaMapper.toEntity(marcaDTO);
        var marcaEntity = marcaRepository.save(marca);

        return marcaMapper.toRecord(marcaEntity);
    }

    public List<Marca> listarMarcas() {
        return marcaRepository.findByOrderByNomeAsc();
    }

    public Marca findMarcaById(Long id) {
        return marcaRepository.findById(id)
                .stream().findFirst()
                .orElseThrow(() -> new ServiceException("Marca não encontrada"));
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
