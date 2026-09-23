import {
  HttpClient,
  HttpErrorResponse,
} from '@angular/common/http';
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

export interface UsuarioCadastroRequest {
  nome: string;
  cpf: string;
  dataNascimento: string;
}
export function perfilUsuarioPendente(
  erro: unknown
): boolean {
  return erro instanceof HttpErrorResponse
    && erro.status === 403;
}

@Injectable({
  providedIn: 'root',
})
export class UsuarioApi {
  private readonly http = inject(HttpClient);
  private readonly baseUrl =
    `${environment.apiUrl}/usuarios`;

  private readonly urlUsuarioAtual =
    `${this.baseUrl}/me`;
  private readonly usuarioAtualState =
    signal<UsuarioAtualResponse | null>(null);

  readonly usuarioAtual =
    this.usuarioAtualState.asReadonly();

  criar(
    request: UsuarioCadastroRequest
  ): Observable<UsuarioAtualResponse> {
    return this.http.post<UsuarioAtualResponse>(
      this.baseUrl,
      request
    ).pipe(
      tap(usuario => {
        this.usuarioAtualState.set(usuario);
      })
    );
  }

  buscarAtual(): Observable<UsuarioAtualResponse> {
    return this.http.get<UsuarioAtualResponse>(
      this.urlUsuarioAtual
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
      this.urlUsuarioAtual,
      request
    ).pipe(
      tap(usuario => {
        this.usuarioAtualState.set(usuario);
      })
    );
  }

  desativar(): Observable<UsuarioAtualResponse> {
    return this.http.patch<UsuarioAtualResponse>(
      `${this.urlUsuarioAtual}/desativar`,
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