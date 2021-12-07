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
   

  findAllCarros(query? : any) : Observable<Page>{
    return this.httpClient.get<Page>(`${this.url}/search?${query}`);
  }

  findCarroById(id : number) : Observable<Carro> {
    return this.httpClient.get<Carro>(`${this.url}/${id}`);
  }

  cadastrarCarro(carro : Carro) : Observable<Carro>{
    if(carro.id) {
      return this.httpClient.put<Carro>(`${this.url}/${carro.id}`,carro);
    }
    else {
      return this.httpClient.post<Carro>(`${this.url}`,carro);
    }
  }

<<<<<<< HEAD
  marcaVendido(carro : Carro) : Observable<Carro> {
=======
  marcaCarroVendido(carro : Carro) : Observable<Carro> {
>>>>>>> 92c568377fb44d4552635849cb4398d38de9ae8b
    return this.httpClient.put<Carro>(`${this.url}/vendido/${carro.id}`, carro);
  }

}
