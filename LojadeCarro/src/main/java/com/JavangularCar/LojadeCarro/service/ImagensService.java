package com.JavangularCar.LojadeCarro.service;

import com.JavangularCar.LojadeCarro.dto.request.ImagensRequest;
import com.JavangularCar.LojadeCarro.dto.response.ImagensResponse;
import com.JavangularCar.LojadeCarro.entity.Imagens;
import com.JavangularCar.LojadeCarro.exception.ImagensException;
import com.JavangularCar.LojadeCarro.mapper.ImagensMapper;
import com.JavangularCar.LojadeCarro.repository.ImagensRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImagensService {

    private final ImagensRepository imagensRepository;
    private final ImagensMapper imagensMapper;
    private final CarroService carroService;
    private final StorageService storageService;

    @Transactional
    public List<ImagensResponse> create(MultipartFile[] files, Long idCarro) throws IOException {

        log.info("Iniciando upload de {} imagem(ns) para o carro {}", files.length, idCarro);

        var carro = carroService.buscaCarro(idCarro);

        List<ImagensResponse> response = new ArrayList<>();

        for (int i = 0; i < files.length; i++) {

            String url = storageService.upload(files[i], idCarro);

            Imagens imagem = new Imagens();
            imagem.setCarro(carro);
            imagem.setUrl(url);

            Imagens salva = imagensRepository.save(imagem);

            if (i == 0 && carro.getUrl() == null) {
                carro.setUrl(url);
            }

            response.add(imagensMapper.toResponse(salva));
        }

        return response;
    }

    public List<ImagensResponse> listarImagens(Long idCarro) {

        log.info("Listando imagens do carro {}", idCarro);


        return imagensRepository
                .findByCarroId(idCarro)
                .map(im -> {
                    return im.stream().map(
                            imagensMapper::toResponse
                    ).toList();
                })
                .orElseThrow(() -> new ImagensException(idCarro));

    }

    public ImagensResponse findImagensById(Long id) {

        log.info("Buscando imagem {}", id);

        return imagensRepository.findById(id)
                .map(imagensMapper::toResponse)
                .orElseThrow(() -> new ImagensException(id));
    }

    @Transactional
    public ImagensResponse updateImagens(ImagensRequest request, Long id) {

        log.info("Atualizando imagem {}", id);

        return imagensRepository.findById(id)
                .map(record -> {

                    record.setCarro(carroService.buscaCarro(request.carroId()));
                    record.setUrl(request.url());

                    Imagens imagemAtualizada = imagensRepository.save(record);

                    return imagensMapper.toResponse(imagemAtualizada);

                }).orElseThrow(() -> new ImagensException(id));
    }

    @Transactional
    public void deleteImagens(Long id) throws IOException {

        log.info("Removendo imagem {}", id);

        var imagem = imagensRepository.findById(id)
                .orElseThrow(() -> new ImagensException(id));

        storageService.delete(imagem.getUrl());

        imagensRepository.delete(imagem);
    }
}
