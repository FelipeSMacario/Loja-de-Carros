import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Usuario } from '../models/usuario.model';

@Injectable({
  providedIn: 'root',
})
export class LoginService {
  constructor(private httpClient: HttpClient) {}

  url: string = 'http://localhost:8080/login';

  logar(query : string) : Observable<Usuario> {
    return this.httpClient.get<Usuario>(`${this.url}?${query}`);
  }
}
