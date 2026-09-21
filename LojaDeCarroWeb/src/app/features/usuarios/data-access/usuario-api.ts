import { HttpClient } from '@angular/common/http';
import {
  inject,
  Injectable,
  signal,
} from '@angular/core';
import {
  Observable,
  tap,
} from 'rxjs';

import { environment } from
  '../../../../environments/environment';

export interface UsuarioAtualResponse {
  id: number;
  nome: string;
  cpf: string;
  dataNascimento: string;
  email: string;
  ativo: boolean;
}

export interface UsuarioUpdateRequest {
  nome: string;
  dataNascimento: string;
}

@Injectable({
  providedIn: 'root',
})
export class UsuarioApi {
  private readonly http = inject(HttpClient);
  private readonly url =
    `${environment.apiUrl}/usuarios/me`;
  private readonly usuarioAtualState =
    signal<UsuarioAtualResponse | null>(null);

  readonly usuarioAtual =
    this.usuarioAtualState.asReadonly();

  buscarAtual(): Observable<UsuarioAtualResponse> {
    return this.http.get<UsuarioAtualResponse>(
      this.url
    ).pipe(
      tap(usuario => {
        this.usuarioAtualState.set(usuario);
      })
    );
  }

  atualizar(
    request: UsuarioUpdateRequest
  ): Observable<UsuarioAtualResponse> {
    return this.http.put<UsuarioAtualResponse>(
      this.url,
      request
    ).pipe(
      tap(usuario => {
        this.usuarioAtualState.set(usuario);
      })
    );
  }

  desativar(): Observable<UsuarioAtualResponse> {
    return this.http.patch<UsuarioAtualResponse>(
      `${this.url}/desativar`,
      null
    ).pipe(
      tap(usuario => {
        this.usuarioAtualState.set(usuario);
      })
    );
  }

  limparUsuarioAtual(): void {
    this.usuarioAtualState.set(null);
  }
}