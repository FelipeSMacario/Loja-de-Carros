export interface VeiculoRequest {
  quilometragem: number;
  valor: number;
  placa: string;
  motor: string;
  descricao: string;
  anoFabricacao: number;
  idsOpcionais: number[];
  idCarroceria: number;
  idCor: number;
  idModelo: number;
  idCombustivel: number;
}