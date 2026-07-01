package com.JavangularCar.LojadeCarro.service;

import com.JavangularCar.LojadeCarro.dto.request.CarroRequest;
import com.JavangularCar.LojadeCarro.dto.response.CarroResponse;
import com.JavangularCar.LojadeCarro.entity.Carro;
import com.JavangularCar.LojadeCarro.exception.CarroException;
import com.JavangularCar.LojadeCarro.mapper.CarroMapper;
import com.JavangularCar.LojadeCarro.repository.CarroRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;


@Slf4j
@Service
@RequiredArgsConstructor
public class CarroService {
    private final CarroRepository carroRepository;

    private final CarroMapper carroMapper;

    private final CarroceriaService carroceriaService;

    private final MarcaService marcaService;

    private final CoresService coresService;

    private final ModeloService modeloService;

    private final UsuarioService usuarioService;

    private final CombustivelService combustivelService;

    public CarroResponse createCarro(CarroRequest request) {
        log.debug("Inicio da createCarroService com a response: {}", request);
        var carroEntity = carroMapper.toEntity(request);
        carroEntity.setCarroceria(carroceriaService.buscaCarroceria(request.idCarroceria()));
        carroEntity.setMarca(marcaService.buscaMarca(request.idMarca()));
        carroEntity.setCores(coresService.buscaCores(request.idCores()));
        carroEntity.setModelo(modeloService.buscaModelo(request.idModelo()));
        carroEntity.setUsuario(usuarioService.buscaUsuario(request.idUsuario()));
        carroEntity.setCombustivel(combustivelService.buscaCombustivel(request.idCombustivel()));
        carroEntity.setDtCadastro(LocalDateTime.now());
        var carro = carroRepository.save(carroEntity);

        log.info("Carro salvo com sucesso!");
        return carroMapper.toRecord(carro);

    }

    public Page<CarroResponse> listarCarros(Pageable pageable) {
        log.info("Inicio da listarCarrosService");
        return carroRepository
                .findAll(pageable)
                .map(carroMapper::toRecord);
    }

    public CarroResponse findCarroById(Long id) {
        log.info("Inicio da findCarroByIdService com id: {}", id);
        return carroRepository.findById(id)
                .map(carroMapper::toRecord)
                .orElseThrow(() -> new CarroException(id));

    }

    public CarroResponse updateCarro(CarroRequest request, Long id) {
        log.info("Inicio da updateCarroService com o id: {}", id);
        return carroRepository.findById(id)
                .map(record -> {
                    carroMapper.toUpdate(request, record);
                    var update = carroRepository.save(record);
                    return carroMapper.toRecord(update);
                })
                .orElseThrow(() -> new CarroException(id));
    }

    public void deleteCarro(Long id) {
        log.info("Inicio da deleteCarroService com o id: {}", id);
        carroRepository.findById(id)
                .orElseThrow(() -> new CarroException(id));

        carroRepository.deleteById(id);

    }

    public Page<CarroResponse> FiltrarCampos(String marca, String modelo, Integer anoInicio, Integer anoFim, Double valorInicio, Double valorFim, Double quilometragem, Pageable pageable) {
        return carroRepository
                .FindByCampos(marca, modelo, anoInicio, anoFim, valorInicio, valorFim, quilometragem, pageable)
                .map(carroMapper::toRecord);
    }


    public CarroResponse macarVendido(CarroRequest request, Long id) {
        log.info("Inicio da macarVendidoService com o id: {}", id);
        return carroRepository.findById(id).map(
                        record -> {
                            carroMapper.toUpdate(request, record);
                            record.setAtivo(false);
                            var update = carroRepository.save(record);
                            return carroMapper.toRecord(update);
                        })
                .orElseThrow(() -> new CarroException(id));
    }

    public Carro buscaCarro(Long id) {
        log.info("Inicio da buscaCarroService com o id: {}", id);
        return carroRepository.findById(id)
                .orElseThrow(() -> new CarroException(id));
    }
}
