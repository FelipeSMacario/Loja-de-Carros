import { VeiculoImagemResponse } from
  './veiculo-detalhe-response';
import { StatusVeiculo } from
  './veiculo-response';

export interface VeiculoEdicaoResponse {
  id: number;
  placa: string;
  quilometragem: number;
  valor: number;
  motor: string;
  descricao: string | null;
  anoFabricacao: number;
  idCarroceria: number;
  idCor: number;
  idModelo: number;
  idCombustivel: number;
  idsOpcionais: number[];
  statusVeiculo: StatusVeiculo;
  imagens: VeiculoImagemResponse[];
}