package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.response.UploadResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StorageService {
    UploadResult upload(MultipartFile file, Long idVeiculo) throws IOException;

    void delete(String objectKey) throws IOException;
}
