import { Carroceria } from "./carroceria.model";
import { Combustivel } from "./combustivel.model";
import { Cores } from "./cores.model";
import { Marca } from "./marca.model";
import { Modelo } from "./modelo.model";
import { Usuario } from "./usuario.model";

export interface Page{
   
        content: Carro[];
        pageable: string;
        totalElements: number;
        last: boolean;
        totalPages: number;
        size: number;
        number: number;
        sort: Sort;
        numberOfElements: number;
        first: boolean;
        empty: boolean;
      
}

export interface Sort {
    sorted: boolean;
    unsorted: boolean;
    empty: boolean;
  }

export class Carro {
    id : number;
    nome : string;
    quilometragem : number;
    url : string;
    valor : number;
    placa : string;
    motor : string;
    anoFabricacao : number;
    carroceria : Carroceria;
    marca : Marca;
    cores : Cores;
    modelo : Modelo;
    usuario : Usuario;
    combustivel : Combustivel;
    
}