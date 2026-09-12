import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { VeiculoDetalheResponse } from '../models/veiculo-detalhe-response';
import { environment } from '../../../../environments/environment';
import { PageResponse } from '../../../core/http/page-response';
import { VeiculoResponse } from '../models/veiculo-response';

@Injectable({
  providedIn: 'root',
})
export class VeiculoApi {
  private readonly http = inject(HttpClient);
  private readonly url = `${environment.apiUrl}/veiculos`;

  listarAtivos(
    page = 0,
    size = 9
  ): Observable<PageResponse<VeiculoResponse>> {
    const params = new HttpParams()
      .set('page', page)
      .set('size', size)
      .set('sort', 'dataCadastro,desc')
      .append('sort', 'id,desc');

    return this.http.get<PageResponse<VeiculoResponse>>(
      this.url,
      { params }
    );
  }
  buscarPorId(id: number): Observable<VeiculoDetalheResponse> {
    return this.http.get<VeiculoDetalheResponse>(
      `${this.url}/${id}`
    );
  }
}