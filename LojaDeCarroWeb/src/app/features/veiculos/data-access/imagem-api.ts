import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../../environments/environment';
import { ImagemResponse } from '../models/imagem-response';

@Injectable({
    providedIn: 'root',
})
export class ImagemApi {
    private readonly http = inject(HttpClient);
    private readonly apiUrl = environment.apiUrl;

    adicionarAoVeiculo(
        idVeiculo: number,
        files: readonly File[]
    ): Observable<ImagemResponse[]> {
        const formData = new FormData();

        files.forEach(file => {
            formData.append('files', file);
        });

        return this.http.post<ImagemResponse[]>(
            `${this.apiUrl}/veiculos/${idVeiculo}/imagens`,
            formData
        );
    }

    excluir(idImagem: number): Observable<void> {
        return this.http.delete<void>(
            `${this.apiUrl}/imagens/${idImagem}`
        );
    }

    definirPrincipal(
        idImagem: number
    ): Observable<void> {
        return this.http.patch<void>(
            `${this.apiUrl}/imagens/${idImagem}/principal`,
            null
        );
    }
    listarPorVeiculo(
        idVeiculo: number
    ): Observable<ImagemResponse[]> {
        return this.http.get<ImagemResponse[]>(
            `${this.apiUrl}/veiculos/${idVeiculo}/imagens`
        );
    }
}