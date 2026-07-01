package com.JavangularCar.LojadeCarro.service;

import com.JavangularCar.LojadeCarro.dto.request.ImagensRequest;
import com.JavangularCar.LojadeCarro.dto.response.ImagensResponse;
import com.JavangularCar.LojadeCarro.entity.Imagens;
import com.JavangularCar.LojadeCarro.exception.ImagensException;
import com.JavangularCar.LojadeCarro.mapper.ImagensMapper;
import com.JavangularCar.LojadeCarro.repository.ImagensRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImagensService {

    private final ImagensRepository imagensRepository;
    private final ImagensMapper imagensMapper;
    private final CarroService carroService;

    public void createImagem(Imagens imagens, MultipartFile files, Long id2) throws IOException {
        var cliente = id2.toString();
        new File("D:\\Loja de carro\\LojaDeCarroAngular\\src\\assets\\TesteArquivoJava\\" + cliente).mkdirs();
        files.transferTo(new File("D:\\Loja de carro\\LojaDeCarroAngular\\src\\assets\\TesteArquivoJava\\" + cliente + "\\" + files.getOriginalFilename()));

        imagens.setUrl("../../../assets/TesteArquivoJava/" + cliente + "/" + files.getOriginalFilename());

        imagensRepository.save(imagens);

    }

    public List<ImagensResponse> listarImagens() {
        log.info("Inicio da listarImagensService");
        return imagensRepository.findAll()
                .stream().map(imagensMapper::toResponse)
                .toList();
    }

    public ImagensResponse findImagensById(Long id) {
        log.info("Inicio da findImagemByIdService com id: {}", id);
        return imagensRepository.findById(id)
                .map(imagensMapper::toResponse)
                .orElseThrow(() -> new ImagensException(id));
    }

    public ImagensResponse updateImagens(ImagensRequest imagens, Long id) {
        log.info("Inicio da updateMarcaService com o id: {}", id);
        return imagensRepository.findById(id)
                .map(record -> {
                    record.setCarro(carroService.buscaCarro(imagens.carroId()));
                    record.setUrl(imagens.url());
                    var update = imagensRepository.save(record);
                    return imagensMapper.toResponse(update);
                }).orElseThrow(() -> new ImagensException(id));
    }

    public void deleteImagens(Long id) {
        log.info("Inicio da deleteImagensService com o id: {}", id);
        var imagem = imagensRepository.findById(id)
                .orElseThrow(() -> new ImagensException(id));

        imagensRepository.deleteById(imagem.getId());
    }

    public void updateEstoque(Long idCarro, Long idImagem) {
        imagensRepository.updateEstoque(idCarro, idImagem);
    }

    public void imagemCarroDefault(String urlDefault, Long idCarro) {
        imagensRepository.imagemCarroDefault(urlDefault, idCarro);
    }

    public void create(MultipartFile[] file, String id) throws IOException {
        for (int c = 0; c < file.length; c++) {
            var imagens = new Imagens();

            //Converter a do carro enviada pelo FORMDATA do angular ID de String para LONG
            var id2 = Long.parseLong(id);

            this.createImagem(imagens, file[c], id2);

            //Função que insere o id do carro após a criação da imagem
            this.updateEstoque(id2, imagens.getId());

            //Função que filtra a primeira imagem inserida e define como imagem Default no carro
            if (c == 0) {
                this.imagemCarroDefault(imagens.getUrl(), id2);
            }
        }
    }
}
