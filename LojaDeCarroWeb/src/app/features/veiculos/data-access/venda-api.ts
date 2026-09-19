import {
  HttpClient,
  HttpParams,
} from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../../environments/environment';
import { PageResponse } from '../../../core/http/page-response';
import {
  StatusVenda,
  VendaRequest,
  VendaResponse,
} from '../models/venda-response';

@Injectable({
  providedIn: 'root',
})
export class VendaApi {
  private readonly http = inject(HttpClient);
  private readonly url = `${environment.apiUrl}/vendas`;

  criar(request: VendaRequest): Observable<VendaResponse> {
    return this.http.post<VendaResponse>(
      this.url,
      request
    );
  }

  listarMinhasCompras(
    pagina: number,
    tamanho: number,
    status?: StatusVenda
  ): Observable<PageResponse<VendaResponse>> {
    return this.http.get<PageResponse<VendaResponse>>(
      `${this.url}/minhas-compras`,
      {
        params: this.criarParametros(
          pagina,
          tamanho,
          status
        ),
      }
    );
  }

  listarMinhasVendas(
    pagina: number,
    tamanho: number,
    status?: StatusVenda
  ): Observable<PageResponse<VendaResponse>> {
    return this.http.get<PageResponse<VendaResponse>>(
      `${this.url}/minhas-vendas`,
      {
        params: this.criarParametros(
          pagina,
          tamanho,
          status
        ),
      }
    );
  }

  cancelar(idVenda: number): Observable<VendaResponse> {
    return this.http.patch<VendaResponse>(
      `${this.url}/${idVenda}/cancelar`,
      null
    );
  }

  concluir(idVenda: number): Observable<VendaResponse> {
    return this.http.patch<VendaResponse>(
      `${this.url}/${idVenda}/concluir`,
      null
    );
  }

  private criarParametros(
    pagina: number,
    tamanho: number,
    status?: StatusVenda
  ): HttpParams {
    let params = new HttpParams()
      .set('page', pagina)
      .set('size', tamanho);

    if (status) {
      params = params.set('status', status);
    }

    return params;
  }
}