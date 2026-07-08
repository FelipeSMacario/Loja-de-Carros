package com.javacar.lojadecarro.service;

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
    public String upload(MultipartFile file, Long idCarro) throws IOException {

        Path pasta = root.resolve(idCarro.toString());

        Files.createDirectories(pasta);

        String nomeArquivo = UUID.randomUUID() + "_" + file.getOriginalFilename();

        Path destino = pasta.resolve(nomeArquivo);

        file.transferTo(destino);

        return "/uploads/" + idCarro + "/" + nomeArquivo;
    }

    @Override
    public void delete(String url) throws IOException {

        Path arquivo = root.resolve(url.replace("/uploads/", ""));

        Files.deleteIfExists(arquivo);
    }
}
