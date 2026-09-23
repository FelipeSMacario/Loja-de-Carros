import { StatusVeiculo } from './veiculo-response';

export interface VeiculoImagemResponse {
  id: number;
  principal: boolean;
}

export interface VendedorResumoResponse {
  id: number;
  nome: string;
}

export interface OpcionalResponse {
  id: number;
  nome: string;
  ativo: boolean;
}

export interface VeiculoDetalheResponse {
  id: number;
  marca: string;
  modelo: string;
  carroceria: string;
  cor: string;
  combustivel: string;
  motor: string;
  valor: number;
  quilometragem: number;
  anoFabricacao: number;
  statusVeiculo: StatusVeiculo;
  descricao: string | null;
  dataCadastro: string;
  vendedor: VendedorResumoResponse;
  imagens: VeiculoImagemResponse[];
  opcionais: OpcionalResponse[];
}