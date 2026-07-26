package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.response.UploadResult;
import com.javacar.lojadecarro.entity.Imagem;
import com.javacar.lojadecarro.entity.Veiculo;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.repository.ImagensRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.javacar.lojadecarro.enums.Entidade.IMAGEM;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImagensService {

    private final ImagensRepository imagensRepository;
    private final StorageService storageService;

    @Transactional
    public List<Imagem> criar(MultipartFile[] files, Veiculo veiculo)
            throws IOException {

        var imagens = new ArrayList<Imagem>();

        for (MultipartFile file : files) {
            var upload = storageService.upload(file, veiculo.getId());
            var imagem = new Imagem(upload);
            imagens.add(imagem);
        }

        try {
            return imagensRepository.saveAll(imagens);
        } catch (Exception e) {

            for (Imagem imagem : imagens) {
                storageService.delete(imagem.getObjectKey());
            }

            throw e;
        }
    }


    @Transactional
    public void definirPrincipal(Long idImagem) {
        var imagem = buscaImagem(idImagem);

        imagensRepository.desmarcarPrincipal(imagem.getVeiculo().getId());

        imagem.setPrincipal(true);
    }

    @Transactional
    public void delete(Long idImagem) throws IOException {
        var imagem = buscaImagem(idImagem);
        storageService.delete(imagem.getObjectKey());
        imagensRepository.delete(imagem);
    }

    public Imagem buscaImagem(Long idImagem) {
        return imagensRepository.findById(idImagem)
                .orElseThrow(() -> new NotFoundException(IMAGEM, idImagem));
    }

}
