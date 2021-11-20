import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Combustivel } from '../models/combustivel.model';

@Injectable({
  providedIn: 'root'
})
export class CombustivelService {

  url : string = "http://localhost:8080/combustivel";

  constructor(private httpClient : HttpClient) { }

  listarCombustivel () : Observable<Combustivel[]> {
    return this.httpClient.get<Combustivel[]>(`${this.url}`);
  }
}
