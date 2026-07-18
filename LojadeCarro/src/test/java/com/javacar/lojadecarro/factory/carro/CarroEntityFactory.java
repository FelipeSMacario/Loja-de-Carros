//package com.javacar.lojadecarro.factory.carro;
//
//import com.javacar.lojadecarro.entity.Veiculo;
//import lombok.RequiredArgsConstructor;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//
//import static lombok.AccessLevel.PRIVATE;
//
//@RequiredArgsConstructor(access = PRIVATE)
//public final class CarroEntityFactory {
//
//    private final Veiculo veiculo;
//
//    private CarroEntityFactory() {
//        this.veiculo = new Veiculo();
//    }
//
//    public static CarroEntityFactory criarEntity() {
//        return new CarroEntityFactory();
//    }
//
//    public CarroEntityFactory comTodosOsCampos() {
//        veiculo.setId(1L);
//        //veiculo.setQuilometragem(67000.98);
//        //veiculo.setUrl("https://bucket/imagens/onix.jpg");
//        veiculo.setValor(new BigDecimal(58000));
//        veiculo.setPlaca("QUV1F836");
//        veiculo.setMotor("1.0");
//        veiculo.setAnoFabricacao(2020);
//       // veiculo.setDtCadastro(LocalDateTime.now());
//      //  veiculo.setAtivo(true);
//
//        return this;
//    }
//
//    public CarroEntityFactory comId(Long id) {
//        veiculo.setId(id);
//        return this;
//    }
//    public CarroEntityFactory comPlaca(String placa) {
//        veiculo.setPlaca(placa);
//        return this;
//    }
//
//    public Veiculo build() {
//        return veiculo;
//    }
//}
