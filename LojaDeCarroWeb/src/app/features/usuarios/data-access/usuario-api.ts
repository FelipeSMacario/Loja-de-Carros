import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from
  '../../../../environments/environment';

export interface UsuarioAtualResponse {
  id: number;
  nome: string;
  cpf: string;
  email: string;
  ativo: boolean;
}

@Injectable({
  providedIn: 'root',
})
export class UsuarioApi {
  private readonly http = inject(HttpClient);
  private readonly url =
    `${environment.apiUrl}/usuarios/me`;

  buscarAtual(): Observable<UsuarioAtualResponse> {
    return this.http.get<UsuarioAtualResponse>(
      this.url
    );
  }

  desativar(): Observable<UsuarioAtualResponse> {
    return this.http.patch<UsuarioAtualResponse>(
      `${this.url}/desativar`,
      null
    );
  }
}