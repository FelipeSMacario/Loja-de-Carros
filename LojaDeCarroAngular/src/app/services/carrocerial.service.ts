import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Carroceria } from '../models/carroceria.model';

@Injectable({
  providedIn: 'root'
})
export class CarrocerialService {

  url : string = "http://localhost:8080/carroceria";

  constructor(private httpCliet : HttpClient) { }

  findAllCarroceria() : Observable<Carroceria[]> {
    return this.httpCliet.get<Carroceria[]>(`${this.url}`);
  }
}
