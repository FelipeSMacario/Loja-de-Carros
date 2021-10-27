import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Marca } from '../models/marca.model';

@Injectable({
  providedIn: 'root'
})
export class MarcasService {

  url : string = "http://localhost:8080/marca";

  constructor(private httpCliente : HttpClient) { }

  public findAllMarcas() : Observable<Marca[]>{
    return this.httpCliente.get<Marca[]>(`${this.url}`);
  }
}
