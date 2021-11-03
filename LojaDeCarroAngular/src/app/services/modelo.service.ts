import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Modelo } from '../models/modelo.model';

@Injectable({
  providedIn: 'root'
})
export class ModeloService {

  url : string = "http://localhost:8080/modelo";

  constructor(private httpCliente : HttpClient) { }

  public findAllModelos() : Observable<Modelo[]>{
    return this.httpCliente.get<Modelo[]>(`${this.url}`);
  }
}
