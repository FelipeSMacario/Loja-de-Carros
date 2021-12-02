import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Usuario } from '../models/usuario.model';

@Injectable({
  providedIn: 'root',
})
export class LoginService {
  constructor(private httpClient: HttpClient) {}

  url: string = 'http://localhost:8080/login';

  logar(username : string, password : string) {
    const headers = new HttpHeaders({Authorization: "Basic " + btoa(username+":"+password)})
    return this.httpClient.get(`${this.url}`, {headers, responseType:"text" as "json"});
  }
}
