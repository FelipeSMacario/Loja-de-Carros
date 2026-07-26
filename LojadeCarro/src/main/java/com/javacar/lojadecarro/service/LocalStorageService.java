package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.response.UploadResult;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class LocalStorageService implements StorageService {

    private final Path root = Paths.get("uploads");

    @Override
    public UploadResult upload(MultipartFile file, Long idVeiculo) throws IOException {

        Path pasta = root.resolve(idVeiculo.toString());
        Files.createDirectories(pasta);

        String nomeOriginal = Paths.get(file.getOriginalFilename())
                .getFileName()
                .toString();

        String objectKey = UUID.randomUUID() + "_" + nomeOriginal;

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

        Files.deleteIfExists(arquivo);
    }
}
