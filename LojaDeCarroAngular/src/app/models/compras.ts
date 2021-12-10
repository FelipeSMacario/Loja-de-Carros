import { Carro } from "./Carro.model";
import { Usuario } from "./usuario.model";

export class Compras{
    id : number;
    comprador : Usuario;
    vendedor : Usuario;
    carro : Carro;
    valor : number;
    dataVenda : Date;
}