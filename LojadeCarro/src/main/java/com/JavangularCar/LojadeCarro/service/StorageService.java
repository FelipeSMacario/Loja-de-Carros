package com.JavangularCar.LojadeCarro.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StorageService {
    String upload(MultipartFile file, Long idCarro) throws IOException;

    void delete(String url) throws IOException;
}
