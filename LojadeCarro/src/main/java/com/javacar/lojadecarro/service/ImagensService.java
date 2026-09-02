package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.entity.Imagem;
import com.javacar.lojadecarro.entity.Veiculo;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.repository.ImagensRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.javacar.lojadecarro.enums.Entidade.IMAGEM;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImagensService {

    private final ImagensRepository imagensRepository;
    private final StorageService storageService;
    private final StorageTransactionSupport storageTransactionSupport;

    @Transactional(rollbackFor = IOException.class )
    public List<Imagem> criar(MultipartFile[] files, Veiculo veiculo)
            throws IOException {
        if (files == null || files.length == 0) {
            return Collections.emptyList();
        }

        var imagens = new ArrayList<Imagem>();

        try {
            for (MultipartFile file : files) {
                var upload = storageService.upload(file, veiculo.getId());

                var imagem = new Imagem(upload);
                veiculo.adicionarImagem(imagem);

                imagens.add(imagem);
            }

            var imagensPersistidas =
                    imagensRepository.saveAll(imagens);

            imagensPersistidas.forEach(imagem ->
                    storageTransactionSupport.deleteOnRollback(
                            imagem.getObjectKey()
                    )
            );
            return imagensPersistidas;

        } catch (IOException | RuntimeException exception) {
            for (Imagem imagem : imagens) {
                veiculo.removerImagem(imagem);

                try {
                    storageService.delete(
                            imagem.getObjectKey()
                    );
                } catch (IOException cleanupException) {
                    exception.addSuppressed(cleanupException);
                }
            }

            throw exception;
        }
    }


    @PreAuthorize(
            "hasRole('ADMIN') or " +
                    "@imagemAuthorization.ehVendedor(#idImagem, authentication)"
    )
    @Transactional
    public void definirPrincipal(Long idImagem) {
        var imagem = buscaImagem(idImagem);
        imagem.getVeiculo().validarPodeSerEditado();
        var imagens = imagensRepository.findByVeiculoId(imagem.getVeiculo().getId());
        imagens.forEach(i -> i.setPrincipal(false));

        imagem.setPrincipal(true);
    }

    @PreAuthorize(
            "hasRole('ADMIN') or " +
                    "@imagemAuthorization.ehVendedor(#idImagem, authentication)"
    )
    @Transactional()
    public void delete(Long idImagem) throws IOException {
        var imagem = buscaImagem(idImagem);
        var veiculo = imagem.getVeiculo();

        veiculo.validarPodeSerEditado();

        var objectKey = imagem.getObjectKey();

        veiculo.removerImagem(imagem);

        imagensRepository.flush();

        storageTransactionSupport.deleteAfterCommit(objectKey);
    }

    public Imagem buscaImagem(Long idImagem) {
        return imagensRepository.findById(idImagem)
                .orElseThrow(() -> new NotFoundException(IMAGEM, idImagem));
    }

}
