package com.JavangularCar.LojadeCarro.service;

import com.JavangularCar.LojadeCarro.dto.request.CarroRequest;
import com.JavangularCar.LojadeCarro.dto.response.CarroResponse;
import com.JavangularCar.LojadeCarro.entity.Carro;
import com.JavangularCar.LojadeCarro.mapper.CarroMapper;
import com.JavangularCar.LojadeCarro.repository.CarroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;


@Service

public class CarroService {
    @Autowired
    CarroRepository carroRepository;

    @Autowired
    CarroMapper carroMapper;

    @Autowired
    private CarroceriaService carroceriaService;

    @Autowired
    private MarcaService marcaService;

    @Autowired
    private CoresService coresService;

    @Autowired
    private ModeloService modeloService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private CombustivelService combustivelService;

    public CarroResponse createCarro(CarroRequest carroDTO) {
        try {
            var carroEntity = carroMapper.toEntity(carroDTO);
            //carroEntity.setCarroceria(carroceriaService.findCarroceriaById(carroDTO.idCarroceria()));
            carroEntity.setMarca(marcaService.findMarcaById(carroDTO.idMarca()));
            carroEntity.setCores(coresService.findCoresById(carroDTO.idCores()));
            carroEntity.setModelo(modeloService.findModeloById(carroDTO.idModelo()));
            carroEntity.setUsuario(usuarioService.findUsuarioBId(carroDTO.idUsuario()));
            carroEntity.setCombustivel(combustivelService.findCombustivelById(carroDTO.idCombustivel()));
            carroEntity.setDtCadastro(LocalDateTime.now());
            var carro = carroRepository.save(carroEntity);

            return carroMapper.toRecord(carro);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
                    record.setUrl(carro.getUrl());
                    record.setValor(carro.getValor());
                    record.setAnoFabricacao(carro.getAnoFabricacao());
                    record.setDtCadastro(carro.getDtCadastro());
                    record.setUsuario(carro.getUsuario());
                    record.setAtivo(carro.isAtivo());
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
