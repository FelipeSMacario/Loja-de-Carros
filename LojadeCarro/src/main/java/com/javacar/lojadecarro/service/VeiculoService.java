package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.request.AlterarStatusRequest;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.javacar.lojadecarro.enums.Entidade.VEICULO;
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
    public VeiculoResponse criar(VeiculoRequest request, MultipartFile[] files) throws IOException {
        var veiculoEntity = veiculoMapper.toEntity(request);
        preencherRelacionamentos(request, veiculoEntity);
        veiculoEntity.setStatusVeiculo(DISPONIVEL);

        var veiculo = veiculoRepository.save(veiculoEntity);
        if (!request.idsOpcionais().isEmpty())
            vincularOpcionais(request, veiculo);

        adicionarImagens(files, veiculo);

        return veiculoMapper.toResponse(veiculo);
    }




    public Page<VeiculoResponse> listar(Pageable pageable, StatusVeiculo statusVeiculo) {
        if (statusVeiculo == null) {
            return veiculoRepository.findAll(pageable)
                    .map(veiculoMapper::toResponse);
        }

        return veiculoRepository.findByStatusVeiculo(statusVeiculo, pageable)
                .map(veiculoMapper::toResponse);
    }

    public VeiculoResponse buscarPorId(Long id) {
        return veiculoMapper.toResponse(buscaVeiculo(id));

    }

    @Transactional
    public VeiculoResponse atualizar(VeiculoRequest request, Long id) {
        var veiculo = buscaVeiculo(id);
        veiculoMapper.toUpdate(request, veiculo);
        preencherRelacionamentos(request, veiculo);
        return veiculoMapper.toResponse(veiculo);
    }


    @Transactional
    public VeiculoResponse alterarStatus(Long id, AlterarStatusRequest request) {
        var veiculo = buscaVeiculo(id);
        veiculo.alterarStatus(request.status());
        return veiculoMapper.toResponse(veiculo);
    }

    public Veiculo buscaVeiculo(Long id) {
        return veiculoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(VEICULO, id));
    }

    public List<ImagensResponse> listarImagens(Long id) {
        return buscaVeiculo(id)
                .getImagens()
                .stream()
                .map(imagensMapper::toResponse)
                .toList();

    }

    private void adicionarImagens(MultipartFile[] files, Veiculo veiculo) throws IOException {
        var imagens = imagensService.create(files, veiculo);

        imagens.forEach(veiculo::adicionarImagem);
    }

    private void vincularOpcionais(VeiculoRequest request, Veiculo veiculo) {
        validaOpcionaisDuplicados(request.idsOpcionais());
        var opcionals = opcionalService.buscarOpcionais(request.idsOpcionais());

        validaOpcionaisExistentes(opcionals, request.idsOpcionais());

        opcionals.forEach(veiculo::adicionarOpcional);
    }


    private void validaOpcionaisDuplicados(List<Long> idsOpcionais) {
        Set<Long> idsUnicos = new HashSet<>(idsOpcionais);

        if (idsUnicos.size() != idsOpcionais.size()) {
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
        var opcionais = opcionalService.buscarOpcionais(ids);
        validaOpcionaisExistentes(opcionais, ids);
        opcionais.forEach(veiculo::adicionarOpcional);
    }
    private void preencherRelacionamentos(VeiculoRequest request, Veiculo veiculoEntity) {
        veiculoEntity.setCarroceria(carroceriaService.buscaCarroceria(request.idCarroceria()));
        veiculoEntity.setCor(coresService.buscaCor(request.idCores()));
        veiculoEntity.setModelo(modeloService.buscaModelo(request.idModelo()));
        veiculoEntity.setVendedor(usuarioService.buscaUsuario(request.idUsuario()));
        veiculoEntity.setCombustivel(combustivelService.buscaCombustivel(request.idCombustivel()));
    }

}
