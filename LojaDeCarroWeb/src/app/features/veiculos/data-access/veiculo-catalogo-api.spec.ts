import {
  provideHttpClient,} from '@angular/common/http';
import { HttpTestingController,  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { firstValueFrom } from 'rxjs';

import { environment } from '../../../../environments/environment';
import { VeiculoCatalogoApi } from './veiculo-catalogo-api';

describe('VeiculoCatalogoApi', () => {
  let service: VeiculoCatalogoApi;
  let httpTesting: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });

    service = TestBed.inject(VeiculoCatalogoApi);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should load all vehicle catalogs', async () => {
    const resultadoPromise = firstValueFrom(
      service.carregar()
    );

    const carroceriasRequest = httpTesting.expectOne(
      `${environment.apiUrl}/carrocerias`
    );
    const combustiveisRequest = httpTesting.expectOne(
      `${environment.apiUrl}/combustiveis`
    );
    const coresRequest = httpTesting.expectOne(
      `${environment.apiUrl}/cores`
    );
    const modelosRequest = httpTesting.expectOne(
      `${environment.apiUrl}/modelos`
    );
    const opcionaisRequest = httpTesting.expectOne(
      `${environment.apiUrl}/opcionais`
    );

    expect(carroceriasRequest.request.method).toBe('GET');
    expect(combustiveisRequest.request.method).toBe('GET');
    expect(coresRequest.request.method).toBe('GET');
    expect(modelosRequest.request.method).toBe('GET');
    expect(opcionaisRequest.request.method).toBe('GET');

    carroceriasRequest.flush([
      { id: 1, nome: 'SUV', ativo: true },
    ]);

    combustiveisRequest.flush([
      { id: 2, nome: 'Flex', ativo: true },
    ]);

    coresRequest.flush([
      { id: 3, nome: 'Branco', ativo: true },
    ]);

    modelosRequest.flush([
      {
        id: 4,
        nome: 'SW4',
        ativo: true,
        marca: {
          id: 5,
          nome: 'Toyota',
          ativo: true,
        },
      },
    ]);

    opcionaisRequest.flush([
      {
        id: 6,
        nome: 'Bancos de couro',
        ativo: true,
      },
    ]);

    expect(await resultadoPromise).toEqual({
      carrocerias: [
        { id: 1, nome: 'SUV', ativo: true },
      ],
      combustiveis: [
        { id: 2, nome: 'Flex', ativo: true },
      ],
      cores: [
        { id: 3, nome: 'Branco', ativo: true },
      ],
      modelos: [
        {
          id: 4,
          nome: 'SW4',
          ativo: true,
          marca: {
            id: 5,
            nome: 'Toyota',
            ativo: true,
          },
        },
      ],
      opcionais: [
        {
          id: 6,
          nome: 'Bancos de couro',
          ativo: true,
        },
      ],
    });
  });
  
});