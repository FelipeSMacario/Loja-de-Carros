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
    public UploadResult upload(MultipartFile file, Long idVeiculo) throws IOException {

        var pasta = root.resolve(idVeiculo.toString());
        Files.createDirectories(pasta);

        String nomeOriginal = Paths.get(file.getOriginalFilename())
                .getFileName()
                .toString();

        var objectKey = UUID.randomUUID() + "_" + nomeOriginal;

        Path destino = pasta.resolve(objectKey).normalize();

        if (!destino.startsWith(pasta)) {
            throw new IllegalArgumentException("Nome de arquivo inválido.");
        }

        file.transferTo(destino);

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

        Path arquivo = root.resolve(objectKey);

        if (!arquivo.startsWith(root)) {
            throw new IllegalArgumentException(
                    "Chave de objeto inválida."
            );
        }

        Files.deleteIfExists(arquivo);
    }
}
