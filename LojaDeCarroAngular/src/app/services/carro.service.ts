import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Carro, Page } from '../models/Carro.model';

@Injectable({
  providedIn: 'root'
})
export class CarroService {

  constructor(private httpClient : HttpClient) { }

  url : string = "http://localhost:8080/carro";
   
  findAllCarros(page? : number) : Observable<Page>{
    return this.httpClient.get<Page>(`${this.url}?page=${page}`);
  }

  findCarroById(id : number) : Observable<Carro> {
    return this.httpClient.get<Carro>(`${this.url}/${id}`);
  }

  findByQuilometragem(valor : number) : Observable<Carro[]> {
    return this.httpClient.get<Carro[]>(`${this.url}/Quilometragem/${valor}`);
  }

}
