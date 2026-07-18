package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.request.AlterarStatusRequest;
import com.javacar.lojadecarro.dto.request.FiltrarCamposCarroRequest;
import com.javacar.lojadecarro.dto.request.VeiculoRequest;
import com.javacar.lojadecarro.dto.response.ImagensResponse;
import com.javacar.lojadecarro.dto.response.VeiculoResponse;
import com.javacar.lojadecarro.entity.Opcional;
import com.javacar.lojadecarro.entity.Veiculo;
import com.javacar.lojadecarro.enums.StatusVeiculo;
import com.javacar.lojadecarro.exception.business.BusinessException;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.mapper.ImagensMapper;
import com.javacar.lojadecarro.mapper.VeiculoMapper;
import com.javacar.lojadecarro.repository.VeiculoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.javacar.lojadecarro.enums.Entidade.*;
import static com.javacar.lojadecarro.enums.StatusVeiculo.DISPONIVEL;


@Slf4j
@Service
@RequiredArgsConstructor
public class VeiculoService {
    private final VeiculoRepository veiculoRepository;

    private final VeiculoMapper veiculoMapper;

    private final CarroceriaService carroceriaService;
    private final OpcionalService opcionalService;

    private final CoresService coresService;

    private final ModeloService modeloService;
    private final ImagensService imagensService;
    private final UsuarioService usuarioService;
    private final CombustivelService combustivelService;
    private final ImagensMapper imagensMapper;

    @Transactional
    public VeiculoResponse createVeiculo(VeiculoRequest request, MultipartFile[] files) throws IOException {
        log.debug("Inicio da createCarroService com a response: {}", request);
        var veiculoEntity = veiculoMapper.toEntity(request);
        veiculoEntity.setCarroceria(carroceriaService.buscaCarroceria(request.idCarroceria()));
        veiculoEntity.setCor(coresService.buscaCores(request.idCores()));
        veiculoEntity.setModelo(modeloService.buscaModelo(request.idModelo()));
        veiculoEntity.setVendedor(usuarioService.buscaUsuario(request.idUsuario()));
        veiculoEntity.setCombustivel(combustivelService.buscaCombustivel(request.idCombustivel()));
        veiculoEntity.setStatusVeiculo(DISPONIVEL);


        var veiculo = veiculoRepository.save(veiculoEntity);
        if (!request.idsOpcionais().isEmpty())
            adicionarOpcionais(request, veiculo);

        vincularImagens(files, veiculo);

        log.info("Carro salvo com sucesso!");
        return veiculoMapper.toResponse(veiculo);

    }


    public Page<VeiculoResponse> listarVeiculos(Pageable pageable) {
        log.info("Inicio da listarCarrosService");
        return veiculoRepository
                .findAll(pageable)
                .map(veiculoMapper::toResponse);
    }

    public VeiculoResponse findVeiculoById(Long id) {
        log.info("Inicio da findCarroByIdService com id: {}", id);
        return veiculoRepository.findById(id)
                .map(veiculoMapper::toResponse)
                .orElseThrow(() -> new NotFoundException(VEICULO, id));

    }

    public VeiculoResponse updateVeiculo(VeiculoRequest request, Long id) {
        log.info("Inicio da updateCarroService com o id: {}", id);
        return veiculoRepository.findById(id)
                .map(carroEntity -> {
                    veiculoMapper.toUpdate(request, carroEntity);
                    var update = veiculoRepository.save(carroEntity);
                    return veiculoMapper.toResponse(update);
                })
                .orElseThrow(() -> new NotFoundException(VEICULO, id));
    }

    public Page<VeiculoResponse> filtrarCampos(FiltrarCamposCarroRequest filtro, Pageable pageable) {
        return veiculoRepository
                .findByCampos(filtro,
                        pageable)
                .map(veiculoMapper::toResponse);
    }


    @Transactional
    public VeiculoResponse alterarStatus(Long id, AlterarStatusRequest request) {
        log.info("Inicio da macarVendidoService com o id: {}", id);
        var veiculo = buscaVeiculo(id);
        veiculo.alterarStatus(request.status());

        return veiculoMapper.toResponse(veiculo);
    }

    public Veiculo buscaVeiculo(Long id) {
        log.info("Inicio da buscaCarroService com o id: {}", id);
        return veiculoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(VEICULO, id));
    }

    public List<ImagensResponse> listarImagens(Long id) {
        List<ImagensResponse> imagens = new ArrayList<>();

        var veiculo = veiculoRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(VEICULO, id));

        veiculo.getImagens().forEach(imagen ->
                imagens.add(imagensMapper.toResponse(imagen)));

        return imagens;

    }

    private void vincularImagens(MultipartFile[] files, Veiculo veiculo) throws IOException {
        var imagens = imagensService.create(files, veiculo);

        imagens.forEach(veiculo::adicionarImagem);
    }

    private void adicionarOpcionais(VeiculoRequest request, Veiculo veiculo) {
        validaOpcionaisDuplicados(request.idsOpcionais());
        var opcionals = opcionalService.buscarOpcionais(request.idsOpcionais());

        validaOpcionaisExistentes(opcionals, request.idsOpcionais());


        opcionals.forEach(veiculo::adicionarOpcional);
    }


    private void validaOpcionaisDuplicados(List<Long> idsOpcionais) {
        Set<Long> idsUnicos = new HashSet<>(idsOpcionais);

        if (idsUnicos.size() > idsOpcionais.size()) {
            throw new BusinessException("A requisição possui opcionais duplicadas.");
        }
    }


    @Transactional
    public void desvincularOpcionais(Long idVeiculo, List<Long> ids) {
        var veiculo = buscaVeiculo(idVeiculo);
        var opcionais = opcionalService.buscarOpcionais(ids);
        validaOpcionaisExistentes(opcionais, ids);
        ids.forEach(veiculo::removerOpcional);

    }

    private void validaOpcionaisExistentes(List<Opcional> opcionals, List<Long> ids) {
        if (opcionals.size() != ids.size()) {
            throw new BusinessException("Um ou mais opcionais não foram encontrados.");
        }
    }

    @Transactional
    public void vincularOpcionais(Long idVeiculo, List<Long> ids) {
        validaOpcionaisDuplicados(ids);
        var veiculo = buscaVeiculo(idVeiculo);
        validaOpcionalJaCadastrada(veiculo, ids);
        var opcionais = opcionalService.buscarOpcionais(ids);
        validaOpcionaisExistentes(opcionais, ids);

        opcionais.forEach(veiculo::adicionarOpcional);
    }

    private void validaOpcionalJaCadastrada(Veiculo veiculo, List<Long> ids) {
        Set<Long> idsJaCadastradas = new HashSet<>(ids);

        veiculo.getOpcionais().forEach(veiculoOpcional -> {
            if (idsJaCadastradas.contains(veiculoOpcional.getOpcional().getId())) {
                throw new BusinessException(OPCIONAL.jaAtiva());
            }
        });
    }
}
