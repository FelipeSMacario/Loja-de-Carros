package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.request.VeiculoRequest;
import com.javacar.lojadecarro.dto.response.ImagemResponse;
import com.javacar.lojadecarro.dto.response.VeiculoResponse;
import com.javacar.lojadecarro.entity.Imagem;
import com.javacar.lojadecarro.entity.Opcional;
import com.javacar.lojadecarro.entity.Veiculo;
import com.javacar.lojadecarro.enums.StatusVeiculo;
import com.javacar.lojadecarro.exception.business.BusinessException;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.mapper.ImagemMapper;
import com.javacar.lojadecarro.mapper.VeiculoMapper;
import com.javacar.lojadecarro.repository.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

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
    private final ImagemMapper imagensMapper;

    @PreAuthorize("isAuthenticated()")
    @Transactional(rollbackFor = IOException.class)
    public VeiculoResponse criar(VeiculoRequest request, MultipartFile[] files, Long idUsuario) throws IOException {
        var vendedor = usuarioService.buscaUsuarioAtivo(idUsuario);
        validarPlacaUnica(request.placa());
        var veiculoEntity = veiculoMapper.toEntity(request);
        preencherRelacionamentos(request, veiculoEntity);
        veiculoEntity.setVendedor(vendedor);
        veiculoEntity.setStatusVeiculo(DISPONIVEL);


        var opcionais = Collections.<Opcional>emptyList();
        if (!request.idsOpcionais().isEmpty()) {
            opcionais = vincularOpcionais(request);
            opcionais.forEach(veiculoEntity::adicionarOpcional);
        }
        var veiculo = veiculoRepository.save(veiculoEntity);

        adicionarImagens(files, veiculo);

        return veiculoMapper.toResponse(veiculo);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public Page<VeiculoResponse> listarAdministrativo(Pageable pageable, StatusVeiculo statusVeiculo) {
        if (statusVeiculo == null) {
            return veiculoRepository.findAll(pageable)
                    .map(veiculoMapper::toResponse);
        }

        return veiculoRepository.findByStatusVeiculo(statusVeiculo, pageable)
                .map(veiculoMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<VeiculoResponse> listarAtivos(Pageable pageable) {
        return veiculoRepository.findByStatusVeiculo(DISPONIVEL, pageable)
                .map(veiculoMapper::toResponse);
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    public Page<VeiculoResponse> listarMeusAnuncios(Pageable pageable, Long idUsuario, StatusVeiculo status) {
        var vendedor = usuarioService.buscaUsuarioAtivo(idUsuario);
        if (status == null) {
            return veiculoRepository
                    .findByVendedor_Id(vendedor.getId(), pageable)
                    .map(veiculoMapper::toResponse);
        }
        return veiculoRepository.findByVendedor_IdAndStatusVeiculo(vendedor.getId(), status, pageable)
                .map(veiculoMapper::toResponse);
    }


    @Transactional(readOnly = true)
    public VeiculoResponse buscarPorId(Long id) {
        return veiculoMapper.toResponse(buscaVeiculoDisponivelPorId(id));

    }

    private Veiculo buscaVeiculoDisponivelPorId(Long id) {
        return veiculoRepository.findByIdAndStatusVeiculo(id, DISPONIVEL)
                .orElseThrow(() -> new NotFoundException(VEICULO, id));
    }

    @PreAuthorize(
            "hasRole('ADMIN') or " +
                    "@veiculoAuthorization.ehVendedor(#id, authentication)"
    )
    @Transactional
    public VeiculoResponse atualizar(VeiculoRequest request, Long id) {
        var veiculo = buscaVeiculo(id);
        veiculo.validarPodeSerEditado();
        if (!request.placa().equals(veiculo.getPlaca())) {
            validarPlacaUnica(request.placa());
        }
        veiculoMapper.toUpdate(request, veiculo);

        preencherRelacionamentosAtualizacao(request, veiculo);
        return veiculoMapper.toResponse(veiculo);
    }

    @PreAuthorize(
            "hasRole('ADMIN') or " +
                    "@veiculoAuthorization.ehVendedor(#id, authentication)"
    )
    @Transactional
    public VeiculoResponse pausarVeiculo(Long id) {
        var veiculo = buscaVeiculo(id);
        veiculo.pausarAnuncio();
        return veiculoMapper.toResponse(veiculo);
    }

    @PreAuthorize(
            "hasRole('ADMIN') or " +
                    "@veiculoAuthorization.ehVendedor(#id, authentication)"
    )
    @Transactional
    public VeiculoResponse reativarVeiculo(Long id) {
        var veiculo = buscaVeiculo(id);
        veiculo.reativarAnuncio();
        return veiculoMapper.toResponse(veiculo);
    }

    public Veiculo buscaVeiculo(Long id) {
        return veiculoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(VEICULO, id));
    }

    @Transactional(readOnly = true)
    public List<ImagemResponse> listarImagens(Long id) {
        return buscaVeiculo(id)
                .getImagens()
                .stream()
                .map(imagensMapper::toResponse)
                .toList();

    }

    @PreAuthorize(
            "hasRole('ADMIN') or " +
                    "@veiculoAuthorization.ehVendedor(#idVeiculo, authentication)"
    )
    @Transactional(rollbackFor = IOException.class)
    public List<ImagemResponse> vincularImagens(Long idVeiculo, MultipartFile[] files) throws IOException {
        var veiculo = buscaVeiculo(idVeiculo);
        veiculo.validarPodeSerEditado();
        var novasImagens = adicionarImagens(files, veiculo);

        return novasImagens.stream()
                .map(imagensMapper::toResponse)
                .toList();
    }

    private List<Imagem> adicionarImagens(
            MultipartFile[] files,
            Veiculo veiculo
    ) throws IOException {
        var imagens = imagensService.criar(files, veiculo);

        imagens.forEach(veiculo::adicionarImagem);
        return imagens;
    }

    private List<Opcional> vincularOpcionais(VeiculoRequest request) {
        validaOpcionaisDuplicados(request.idsOpcionais());
        var opcionals = opcionalService.buscarOpcionaisAtivos(request.idsOpcionais());

        validaOpcionaisExistentes(opcionals, request.idsOpcionais());

        return opcionals;
    }


    private void validaOpcionaisDuplicados(List<Long> idsOpcionais) {
        Set<Long> idsUnicos = new HashSet<>(idsOpcionais);

        if (idsUnicos.size() != idsOpcionais.size()) {
            throw new BusinessException("A requisição possui opcionais duplicadas.");
        }
    }

    @PreAuthorize(
            "hasRole('ADMIN') or " +
                    "@veiculoAuthorization.ehVendedor(#idVeiculo, authentication)"
    )
    @Transactional
    public void desvincularOpcionais(Long idVeiculo, List<Long> ids) {
        validaOpcionaisDuplicados(ids);
        var veiculo = buscaVeiculo(idVeiculo);
        veiculo.validarPodeSerEditado();
        var opcionais = opcionalService.buscarOpcionais(ids);
        validaOpcionaisExistentes(opcionais, ids);
        ids.forEach(veiculo::removerOpcional);

    }

    private void validaOpcionaisExistentes(List<Opcional> opcionals, List<Long> ids) {
        if (opcionals.size() != ids.size()) {
            throw new BusinessException("Um ou mais opcionais não foram encontrados.");
        }
    }

    @PreAuthorize(
            "hasRole('ADMIN') or " +
                    "@veiculoAuthorization.ehVendedor(#idVeiculo, authentication)"
    )
    @Transactional
    public void vincularOpcionais(Long idVeiculo, List<Long> ids) {
        validaOpcionaisDuplicados(ids);
        var veiculo = buscaVeiculo(idVeiculo);
        veiculo.validarPodeSerEditado();
        var opcionais = opcionalService.buscarOpcionaisAtivos(ids);
        validaOpcionaisExistentes(opcionais, ids);
        opcionais.forEach(veiculo::adicionarOpcional);
    }

    private void preencherRelacionamentos(VeiculoRequest request, Veiculo veiculoEntity) {
            veiculoEntity.setCarroceria(carroceriaService.buscaCarroceriaAtiva(request.idCarroceria()));
            veiculoEntity.setCor(coresService.buscaCorAtiva(request.idCores()));
            veiculoEntity.setModelo(modeloService.buscaModeloAtivo(request.idModelo()));
            veiculoEntity.setCombustivel(combustivelService.buscaCombustivelAtivo(request.idCombustivel()));

    }

    private void preencherRelacionamentosAtualizacao(VeiculoRequest request, Veiculo veiculoEntity) {
        if (!Objects.equals(request.idCarroceria(), veiculoEntity.getCarroceria().getId())) {
            veiculoEntity.setCarroceria(carroceriaService.buscaCarroceriaAtiva(request.idCarroceria()));
        }

        if (!Objects.equals(request.idCores(), veiculoEntity.getCor().getId())) {
            veiculoEntity.setCor(coresService.buscaCorAtiva(request.idCores()));
        }

        if (!Objects.equals(request.idModelo(), veiculoEntity.getModelo().getId())) {
            veiculoEntity.setModelo(modeloService.buscaModeloAtivo(request.idModelo()));
        }

        if (!Objects.equals(request.idCombustivel(), veiculoEntity.getCombustivel().getId())) {
            veiculoEntity.setCombustivel(combustivelService.buscaCombustivelAtivo(request.idCombustivel()));
        }

    }

    private void validarPlacaUnica(String placa) {
        if (veiculoRepository.existsByPlaca(placa)) {
            throw new BusinessException("A placa informada já possui um cadastro.");
        }
    }

}
