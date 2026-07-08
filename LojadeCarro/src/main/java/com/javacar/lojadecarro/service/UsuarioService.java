package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.request.UsuarioRequest;
import com.javacar.lojadecarro.dto.response.UsuarioResponse;
import com.javacar.lojadecarro.entity.Usuario;
import com.javacar.lojadecarro.exception.UsuarioException;
import com.javacar.lojadecarro.mapper.UsuarioMapper;
import com.javacar.lojadecarro.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final BCryptPasswordEncoder encoder;


    public UsuarioResponse createUsuario(UsuarioRequest usuario) {
        log.debug("Inicio da createUsuarioService com a response: {}", usuario);
        var usuarioEntity = usuarioMapper.toEntity(usuario);
        usuarioEntity.setPassword(encoder.encode(usuario.password()));
        var usuarioResponse = usuarioRepository.save(usuarioEntity);

        log.info("Usuário salva com sucesso!");

        return usuarioMapper.toResponse(usuarioResponse);
    }

    public List<UsuarioResponse> listarUsuario() {
        log.info("Inicio da listarUsuarioService");
        return usuarioRepository.findAll()
                .stream().map(usuarioMapper::toResponse)
                .toList();
    }

    public UsuarioResponse findUsuarioBId(Long id) {
        log.info("Inicio da findMarcaByIdService com id: {}", id);
        return usuarioRepository.findById(id)
                .map(usuarioMapper::toResponse)
                .orElseThrow(() -> new UsuarioException(id));
    }

    public UsuarioResponse updateUsuario(@RequestBody UsuarioRequest usuario, Long id) {
        log.info("Inicio da updateUsuarioService com o id: {}", id);
        return usuarioRepository.findById(id)
                .map(record -> {
                    record.setCpf(usuario.cpf());
                    record.setDtNascimento(usuario.dtNascimento());
                    record.setNome(usuario.nome());
                    record.setEmail(usuario.email());
                    record.setPassword(encoder.encode(usuario.password()));
                    var update = usuarioRepository.save(record);
                    return usuarioMapper.toResponse(update);
                }).orElseThrow(() -> new UsuarioException(id));
    }

    public void deleteUsuario(Long id) {
        log.info("Inicio da deleteUsuarioService com o id: {}", id);
        usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioException(id));

        usuarioRepository.deleteById(id);
    }

    public Usuario buscaUsuario(Long id) {
        log.info("Inicio da buscaUsuarioService com o id: {}", id);
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioException(id));
    }
}
