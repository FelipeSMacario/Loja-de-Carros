import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Compras } from '../models/compras';

@Injectable({
  providedIn: 'root'
})
export class ComprarService {

  url : string = "http://localhost:8080/compras";

  constructor(
    private httpCliet : HttpClient
  ) { }

  comprar(compras : Compras) : Observable<Compras>{
    return this.httpCliet.post<Compras>(this.url, compras);
  }
}
