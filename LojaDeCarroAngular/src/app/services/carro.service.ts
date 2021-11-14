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

  teste(query? : any) : Observable<Page>{
    return this.httpClient.get<Page>(`${this.url}/search?${query}`);
  }

  filtrarCarros(marca : string) : Observable<Page> {
    return this.httpClient.get<Page>(`${this.url}/search?marca=${marca}`)
  }

  findMarca(marca : string) : Observable<Carro[]>{
    return this.httpClient.get<Carro[]>(`${this.url}/Marca/${marca}`);
  }

  findCarroById(id : number) : Observable<Carro> {
    return this.httpClient.get<Carro>(`${this.url}/${id}`);
  }

  findByQuilometragem(valor : number) : Observable<Carro[]> {
    return this.httpClient.get<Carro[]>(`${this.url}/Quilometragem/${valor}`);
  }

}
