import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Kit } from '../models/kit.model';

@Injectable({
  providedIn: 'root'
})
export class KitService {

  constructor(private httpClient : HttpClient) { }

  url : string = "http://localhost:8080/kit";
   
  findAllKits() : Observable<Kit[]>{
    return this.httpClient.get<Kit[]>(`${this.url}`);
  }

  findKitById(id : number) : Observable<Kit>{
    return this.httpClient.get<Kit>(`${this.url}/${id}`);
  }
}
