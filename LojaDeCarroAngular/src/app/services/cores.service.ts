import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Cores } from '../models/cores.model';

@Injectable({
  providedIn: 'root'
})
export class CoresService {

  url : string = "http://localhost:8080/cores";

  constructor(
    private httpClient : HttpClient
  ) { }

  findAllCores() : Observable<Cores[]>{
    return this.httpClient.get<Cores[]>(`${this.url}`);
  }
  findCores(id : number) : Observable<Cores>{
    return this.httpClient.get<Cores>(`${this.url}/${id}`);
  }
}
