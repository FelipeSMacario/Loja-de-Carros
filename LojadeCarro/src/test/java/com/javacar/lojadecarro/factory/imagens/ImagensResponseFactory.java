//package com.javacar.lojadecarro.factory.imagens;
//
//import com.javacar.lojadecarro.dto.response.ImagensResponse;
//
//public class ImagensResponseFactory {
//    private Long id;
//    private String url;
//    private Long idCarro;
//
//    private ImagensResponseFactory() {
//    }
//
//    public static ImagensResponseFactory criarResponse() {
//        return new ImagensResponseFactory();
//    }
//
//    public ImagensResponseFactory comTodosOsCampos() {
//        this.id = 1L;
//        this.url = "https://bucket/imagens/onix.jpg";
//        this.idCarro = 1L;
//        return this;
//    }
//
//    public ImagensResponseFactory comId(Long id) {
//        this.id = id;
//        return this;
//    }
//
//    public ImagensResponseFactory comURL(String url) {
//        this.url = url;
//        return this;
//    }
//
//    public ImagensResponseFactory comIdCarro(Long idCarro) {
//        this.idCarro = idCarro;
//        return this;
//    }
//
//    public ImagensResponse build() {
//        return new ImagensResponse(
//                id,
//                url,
//                idCarro);
//    }
//}
