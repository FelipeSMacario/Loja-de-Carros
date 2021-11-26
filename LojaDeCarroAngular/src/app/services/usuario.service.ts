import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/internal/Observable';
import { Usuario } from '../models/usuario.model';

@Injectable({
  providedIn: 'root'
})
export class UsuarioService {

  constructor(private httpClient : HttpClient) { }

  url : string = "http://localhost:8080/usuario";
   
  findAllUsuarios() : Observable<Usuario[]>{
    return this.httpClient.get<Usuario[]>(`${this.url}`);
  }

  findUsuarioById(id : number) : Observable<Usuario> {
    return this.httpClient.get<Usuario>(`${this.url}/${id}`);
  }

  saveUsuario(usuario : Usuario) : Observable<Usuario> {
    if(usuario.id) {
      return this.httpClient.put<Usuario>(`${this.url}/${usuario.id}`,usuario);
    }
    else {
      return this.httpClient.post<Usuario>(`${this.url}`,usuario);
    }
  }
}
