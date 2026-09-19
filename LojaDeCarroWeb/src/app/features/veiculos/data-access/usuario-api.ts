import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from
  '../../../../environments/environment';

export interface UsuarioAtualResponse {
  id: number;
}

@Injectable({
  providedIn: 'root',
})
export class UsuarioApi {
  private readonly http = inject(HttpClient);

  buscarAtual(): Observable<UsuarioAtualResponse> {
    return this.http.get<UsuarioAtualResponse>(
      `${environment.apiUrl}/usuarios/me`
    );
  }
}