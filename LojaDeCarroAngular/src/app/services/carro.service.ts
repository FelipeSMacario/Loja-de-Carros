import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Carro } from '../models/Carro.model';

@Injectable({
  providedIn: 'root'
})
export class CarroService {

  constructor(private httpClient : HttpClient) { }

  url : string = "http://localhost:8080/carro";
   
  findAllCarros() : Observable<Carro[]>{
    return this.httpClient.get<Carro[]>(`${this.url}`);
  }

  findCarroById(id : number) : Observable<Carro> {
    return this.httpClient.get<Carro>(`${this.url}/${id}`);
  }

  findByMarca(marca : string) : Observable<Carro[]> {
    return this.httpClient.get<Carro[]>(`${this.url}/Marca/${marca}`);
  }
  findByModelo(marca : string, modelo : string) : Observable<Carro[]> {
    return this.httpClient.get<Carro[]>(`${this.url}/Marca/${marca}/Modelo/${modelo}`);
  }
  findByValor(valor1 : number, valor2 : number) : Observable<Carro[]> {
    return this.httpClient.get<Carro[]>(`${this.url}/Valor/${valor1}/${valor2}`);
  }

  findByAno(anoInicio : number, anoFim : number) : Observable<Carro[]>{
    return this.httpClient.get<Carro[]>(`${this.url}/AnoCarro/${anoInicio}/${anoFim}`);
  }

  findByQuilometragem(valor : number) : Observable<Carro[]> {
    return this.httpClient.get<Carro[]>(`${this.url}/Quilometragem/${valor}`);
  }
}
