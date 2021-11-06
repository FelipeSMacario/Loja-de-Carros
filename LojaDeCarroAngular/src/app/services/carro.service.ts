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
}
