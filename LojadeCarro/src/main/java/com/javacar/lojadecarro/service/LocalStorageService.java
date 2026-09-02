package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.response.UploadResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class LocalStorageService implements StorageService {
    private final Path root;

    public LocalStorageService(@Value("${storage.local.root:uploads}") String root) {
        this.root = Paths.get(root)
                .toAbsolutePath()
                .normalize();
    }

    @Override
    public UploadResult upload(
            MultipartFile file,
            Long idVeiculo
    ) throws IOException {

        if (file == null) {
            throw new IllegalArgumentException(
                    "Arquivo é obrigatório."
            );
        }

        if (idVeiculo == null) {
            throw new IllegalArgumentException(
                    "Identificação do veículo é obrigatória."
            );
        }

        var nomeInformado = file.getOriginalFilename();

        if (nomeInformado == null || nomeInformado.isBlank()) {
            throw new IllegalArgumentException(
                    "Nome original do arquivo é obrigatório."
            );
        }

        var nomeOriginal = Paths.get(nomeInformado)
                .getFileName()
                .toString();

        var pasta = root.resolve(idVeiculo.toString())
                .normalize();

        if (!pasta.startsWith(root)) {
            throw new IllegalArgumentException(
                    "Diretório do veículo inválido."
            );
        }

        Files.createDirectories(pasta);

        var objectKey = UUID.randomUUID()
                + "_"
                + nomeOriginal;

        var destino = pasta.resolve(objectKey)
                .normalize();

        if (!destino.startsWith(pasta)) {
            throw new IllegalArgumentException(
                    "Nome de arquivo inválido."
            );
        }

        try {
            file.transferTo(destino);
        } catch (IOException | RuntimeException exception) {
            try {
                Files.deleteIfExists(destino);
            } catch (IOException cleanupException) {
                exception.addSuppressed(cleanupException);
            }

            throw exception;
        }

        return new UploadResult(
                idVeiculo + "/" + objectKey,
                "uploads",
                nomeOriginal,
                file.getContentType(),
                file.getSize()
        );
    }

    @Override
    public void delete(String objectKey) throws IOException {

            var arquivo = root.resolve(objectKey)
                    .normalize();

            if (!arquivo.startsWith(root)) {
                throw new IllegalArgumentException(
                        "Chave de objeto inválida."
                );
            }

            Files.deleteIfExists(arquivo);
        }
}
