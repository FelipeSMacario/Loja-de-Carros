import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Imagens } from '../models/imagens.model';

@Injectable({
  providedIn: 'root'
})
export class ImagensService {

  url : string = "http://localhost:8080/imagens";

  constructor(private httpClient : HttpClient) { }

  findAllImagens() : Observable<Imagens[]> {
    return this.httpClient.get<Imagens[]>(`${this.url}`);
  }

  findImagensById(id : number) : Observable<Imagens> {
    return this.httpClient.get<Imagens>(`${this.url}/${id}`);
  }
}
