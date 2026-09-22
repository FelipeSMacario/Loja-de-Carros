import { HttpClient } from '@angular/common/http';
import {
    inject,
    Injectable,
} from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from
    '../../../../environments/environment';
import {
    UsuarioAtualResponse,
} from './usuario-api';

export type StatusFiltroUsuario =
    | 'TODAS'
    | 'ATIVAS'
    | 'INATIVAS';

@Injectable({
    providedIn: 'root',
})
export class AdminUsuarioApi {
    private readonly http = inject(HttpClient);
    private readonly url =
        `${environment.apiUrl}/admin/usuarios`;

    listar(
        status: StatusFiltroUsuario = 'TODAS'
    ): Observable<UsuarioAtualResponse[]> {
        return this.http.get<UsuarioAtualResponse[]>(
            this.url,
            {
                params: {
                    status,
                },
            }
        );
    }

    buscarPorId(
        id: number
    ): Observable<UsuarioAtualResponse> {
        return this.http.get<UsuarioAtualResponse>(
            `${this.url}/${id}`
        );
    }

    alterarStatus(
        id: number,
        ativo: boolean
    ): Observable<UsuarioAtualResponse> {
        return this.http.patch<UsuarioAtualResponse>(
            `${this.url}/${id}/status`,
            { ativo }
        );
    }
}