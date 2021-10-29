import { Carro } from "./Carro.model";
import { Usuario } from "./usuario.model";

export class Venda {
    id : number;
    data : Date;
    usuario : Usuario;
    carro : Carro;
}