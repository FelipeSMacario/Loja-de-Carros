package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.request.CarroRequest;
import com.javacar.lojadecarro.dto.request.FiltrarCamposCarroRequest;
import com.javacar.lojadecarro.dto.response.CarroResponse;
import com.javacar.lojadecarro.entity.Carro;
import com.javacar.lojadecarro.exception.CarroException;
import com.javacar.lojadecarro.mapper.CarroMapper;
import com.javacar.lojadecarro.repository.CarroRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
        return carroMapper.toResponse(carro);

    }

    public Page<CarroResponse> listarCarros(Pageable pageable) {
        log.info("Inicio da listarCarrosService");
        return carroRepository
                .findAll(pageable)
                .map(carroMapper::toResponse);
    }

    public CarroResponse findCarroById(Long id) {
        log.info("Inicio da findCarroByIdService com id: {}", id);
        return carroRepository.findById(id)
                .map(carroMapper::toResponse)
                .orElseThrow(() -> new CarroException(id));

    }

    public CarroResponse updateCarro(CarroRequest request, Long id) {
        log.info("Inicio da updateCarroService com o id: {}", id);
        return carroRepository.findById(id)
                .map(record -> {
                    carroMapper.toUpdate(request, record);
                    var update = carroRepository.save(record);
                    return carroMapper.toResponse(update);
                })
                .orElseThrow(() -> new CarroException(id));
    }

    public void deleteCarro(Long id) {
        log.info("Inicio da deleteCarroService com o id: {}", id);
        carroRepository.findById(id)
                .orElseThrow(() -> new CarroException(id));

        carroRepository.deleteById(id);

    }

    public Page<CarroResponse> filtrarCampos(FiltrarCamposCarroRequest filtro, Pageable pageable) {
        return carroRepository
                .FindByCampos(filtro.marca(),
                        filtro.modelo(),
                        filtro.anoInicio(),
                        filtro.anoFim(),
                        filtro.valorInicio(),
                        filtro.valorFim(),
                        filtro.quilometragem(),
                        pageable)
                .map(carroMapper::toResponse);
    }


    public CarroResponse marcarVendido(CarroRequest request, Long id) {
        log.info("Inicio da macarVendidoService com o id: {}", id);
        return carroRepository.findById(id).map(
                        record -> {
                            carroMapper.toUpdate(request, record);
                            record.setAtivo(false);
                            var update = carroRepository.save(record);
                            return carroMapper.toResponse(update);
                        })
                .orElseThrow(() -> new CarroException(id));
    }

    public Carro buscaCarro(Long id) {
        log.info("Inicio da buscaCarroService com o id: {}", id);
        return carroRepository.findById(id)
                .orElseThrow(() -> new CarroException(id));
    }
}
