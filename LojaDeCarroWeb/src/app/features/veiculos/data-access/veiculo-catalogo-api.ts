import { HttpClient } from '@angular/common/http';
import {
  inject,
  Injectable,
} from '@angular/core';
import {
  forkJoin,
  Observable,
} from 'rxjs';

import { environment } from '../../../../environments/environment';
import {
  CatalogoItem,
  ModeloCatalogo,
  VeiculoCatalogos,
} from '../models/veiculo-catalogos';

@Injectable({
  providedIn: 'root',
})
export class VeiculoCatalogoApi {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = environment.apiUrl;

  carregar(): Observable<VeiculoCatalogos> {
    return forkJoin({
      carrocerias: this.http.get<CatalogoItem[]>(
        `${this.apiUrl}/carrocerias`
      ),
      combustiveis: this.http.get<CatalogoItem[]>(
        `${this.apiUrl}/combustiveis`
      ),
      cores: this.http.get<CatalogoItem[]>(
        `${this.apiUrl}/cores`
      ),
      modelos: this.http.get<ModeloCatalogo[]>(
        `${this.apiUrl}/modelos`
      ),
      opcionais: this.http.get<CatalogoItem[]>(
        `${this.apiUrl}/opcionais`
      ),
    });
  }
}