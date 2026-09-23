import { StatusVeiculo } from  '../../veiculos/models/veiculo-response';

export type StatusVenda =
  | 'EM_ANDAMENTO'
  | 'CONCLUIDA'
  | 'CANCELADA';

export interface VendaRequest {
  veiculoId: number;
}

export interface VeiculoVendaResponse {
  id: number;
  marca: string;
  modelo: string;
  status: StatusVeiculo;
}

export interface UsuarioResumoResponse {
  id: number;
  nome: string;
}

export interface VendaResponse {
  id: number;
  valorVenda: number;
  statusVenda: StatusVenda;
  dataVenda: string;
  veiculo: VeiculoVendaResponse;
  vendedor: UsuarioResumoResponse;
  comprador: UsuarioResumoResponse;
}