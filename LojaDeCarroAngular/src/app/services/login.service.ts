import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Usuario } from '../models/usuario.model';

@Injectable({
  providedIn: 'root',
})
export class LoginService {
  constructor(private httpClient: HttpClient) {}

  url: string = 'http://localhost:8080/usuario';
  url2: string = 'http://localhost:8080/login?';
  

  login(username: string, password: string) : Observable<Usuario> {
    
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type':  'application/json',
        'Authorization': 'Basic ' + btoa(username + ":" + password)
      })
    };
    return this.httpClient.get<Usuario>(this.url, httpOptions); 
  }

  getUser(queryParams : string) : Observable<Usuario>{
    return this.httpClient.get<Usuario>(`${this.url2}${queryParams}`);
  }

  

}
