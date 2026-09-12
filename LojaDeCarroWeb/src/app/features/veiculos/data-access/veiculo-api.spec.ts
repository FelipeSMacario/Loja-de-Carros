import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { firstValueFrom } from 'rxjs';
import { VeiculoDetalheResponse } from '../models/veiculo-detalhe-response';
import { environment } from '../../../../environments/environment';
import { PageResponse } from '../../../core/http/page-response';
import { VeiculoResponse } from '../models/veiculo-response';
import { VeiculoApi } from './veiculo-api';

describe('VeiculoApi', () => {
  let service: VeiculoApi;
  let httpTesting: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });

    service = TestBed.inject(VeiculoApi);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should list active vehicles with pagination', async () => {
    const veiculo: VeiculoResponse = {
      id: 1,
      placa: 'ABC1D23',
      marca: 'Chevrolet',
      modelo: 'Onix',
      carroceria: 'Hatch',
      cor: 'Branco',
      combustivel: 'Flex',
      valor: 75990,
      quilometragem: 70000,
      anoFabricacao: 2023,
      statusVeiculo: 'DISPONIVEL',
      imagemPrincipalId: 10,
      descricao: 'Veículo em ótimo estado',
    };

    const resposta: PageResponse<VeiculoResponse> = {
      content: [veiculo],
      totalElements: 1,
      totalPages: 1,
      size: 9,
      number: 0,
      numberOfElements: 1,
      first: true,
      last: true,
      empty: false,
    };

    const resultadoPromise = firstValueFrom(
      service.listarAtivos(0, 9)
    );

    const request = httpTesting.expectOne(req =>
      req.url === `${environment.apiUrl}/veiculos`
      && req.params.get('page') === '0'
      && req.params.get('size') === '9'
      && req.params.get('sort') === 'dataCadastro,desc'
    );

    expect(request.request.method).toBe('GET');

    request.flush(resposta);

    expect(await resultadoPromise).toEqual(resposta);
    expect(request.request.params.get('page')).toBe('0');
    expect(request.request.params.get('size')).toBe('9');

    expect(request.request.params.getAll('sort')).toEqual([
      'dataCadastro,desc',
      'id,desc',
    ]);
  });

  it('should find vehicle details by id', async () => {
    const resposta: VeiculoDetalheResponse = {
      id: 1,
      marca: 'Chevrolet',
      modelo: 'Onix',
      carroceria: 'Hatch',
      cor: 'Branco',
      combustivel: 'Flex',
      motor: '1.0',
      valor: 75990,
      quilometragem: 70000,
      anoFabricacao: 2023,
      statusVeiculo: 'DISPONIVEL',
      descricao: 'Veículo em ótimo estado',
      dataCadastro: '2026-09-12T13:45:30',
      vendedor: {
        id: 5,
        nome: 'Felipe',
      },
      imagens: [
        {
          id: 10,
          principal: true,
        },
        {
          id: 11,
          principal: false,
        },
      ],
      opcionais: [
        {
          id: 1,
          nome: 'Freio ABS',
          ativo: true,
        },
        {
          id: 2,
          nome: 'Multimídia',
          ativo: true,
        },
      ],
    };

    const resultadoPromise = firstValueFrom(
      service.buscarPorId(1)
    );

    const request = httpTesting.expectOne(
      `${environment.apiUrl}/veiculos/1`
    );

    expect(request.request.method).toBe('GET');
    expect(request.request.params.keys()).toHaveLength(0);

    request.flush(resposta);

    expect(await resultadoPromise).toEqual(resposta);
  });
});