export type StatusVeiculo =
  | 'DISPONIVEL'
  | 'PAUSADO'
  | 'RESERVADO'
  | 'VENDIDO';

export interface VeiculoResponse {
  id: number;
  placa: string;
  marca: string;
  modelo: string;
  carroceria: string;
  cor: string;
  combustivel: string;
  valor: number;
  quilometragem: number;
  anoFabricacao: number;
  statusVeiculo: StatusVeiculo;
  imagemPrincipalId: number | null;
  descricao: string | null;
}