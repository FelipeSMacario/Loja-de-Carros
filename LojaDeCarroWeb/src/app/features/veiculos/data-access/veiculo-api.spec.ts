import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting, } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { firstValueFrom } from 'rxjs';
import { VeiculoDetalheResponse } from '../models/veiculo-detalhe-response';
import { environment } from '../../../../environments/environment';
import { PageResponse } from '../../../core/http/page-response';
import { VeiculoResponse } from '../models/veiculo-response';
import { VeiculoApi } from './veiculo-api';
import { VeiculoRequest } from '../models/veiculo-request';

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
  it('should create a vehicle using multipart form data', async () => {
    const request: VeiculoRequest = {
      quilometragem: 28000,
      valor: 350000,
      placa: 'ABC1D23',
      motor: '2.0',
      descricao: 'Veículo em ótimo estado',
      anoFabricacao: 2022,
      idsOpcionais: [1, 2],
      idCarroceria: 1,
      idCor: 2,
      idModelo: 3,
      idCombustivel: 4,
    };

    const files = [
      new File(
        ['imagem-principal'],
        'principal.jpg',
        { type: 'image/jpeg' }
      ),
      new File(
        ['imagem-secundaria'],
        'secundaria.png',
        { type: 'image/png' }
      ),
    ];

    service.criar(request, files).subscribe();

    const httpRequest = httpTesting.expectOne(
      `${environment.apiUrl}/veiculos`
    );

    expect(httpRequest.request.method).toBe('POST');

    expect(
      httpRequest.request.headers.has('Content-Type')
    ).toBe(false);

    const formData =
      httpRequest.request.body as FormData;

    const requestPart = formData.get('request');

    expect(requestPart).toBeInstanceOf(Blob);

    if (!(requestPart instanceof Blob)) {
      throw new Error(
        'A parte request deveria ser um Blob'
      );
    }

    expect(requestPart.type).toBe('application/json');

    const requestRecebido = JSON.parse(
      await requestPart.text()
    );

    expect(requestRecebido).toEqual(request);

    const filesRecebidos =
      formData.getAll('files') as File[];

    expect(
      filesRecebidos.map(file => file.name)
    ).toEqual([
      'principal.jpg',
      'secundaria.png',
    ]);

    httpRequest.flush({});
  });

  it('should create a vehicle without images', () => {
    const request: VeiculoRequest = {
      quilometragem: 0,
      valor: 75000,
      placa: 'DEF4G56',
      motor: '1.0',
      descricao: 'Veículo sem imagens',
      anoFabricacao: 2024,
      idsOpcionais: [],
      idCarroceria: 1,
      idCor: 2,
      idModelo: 3,
      idCombustivel: 4,
    };

    service.criar(request, []).subscribe();

    const httpRequest = httpTesting.expectOne(
      `${environment.apiUrl}/veiculos`
    );

    const formData =
      httpRequest.request.body as FormData;

    expect(formData.has('request')).toBe(true);
    expect(formData.has('files')).toBe(false);

    httpRequest.flush({});
  });

  it('should list own vehicles filtered by status', () => {
    service.listarMeusAnuncios(
      2,
      6,
      'PAUSADO'
    ).subscribe();

    const request = httpTesting.expectOne(req =>
      req.url ===
      `${environment.apiUrl}/veiculos/meus-anuncios`
      && req.params.get('page') === '2'
      && req.params.get('size') === '6'
      && req.params.get('status') === 'PAUSADO'
    );

    expect(request.request.method).toBe('GET');

    expect(request.request.params.getAll('sort'))
      .toEqual([
        'dataCadastro,desc',
        'id,desc',
      ]);

    request.flush({
      content: [],
      totalElements: 0,
      totalPages: 0,
      size: 6,
      number: 2,
      numberOfElements: 0,
      first: false,
      last: true,
      empty: true,
    });
  });

  it('should list own vehicles without a status filter', () => {
    service.listarMeusAnuncios().subscribe();

    const request = httpTesting.expectOne(req =>
      req.url ===
      `${environment.apiUrl}/veiculos/meus-anuncios`
    );

    expect(request.request.params.has('status'))
      .toBe(false);

    expect(request.request.params.get('page'))
      .toBe('0');

    expect(request.request.params.get('size'))
      .toBe('9');

    request.flush({
      content: [],
      totalElements: 0,
      totalPages: 0,
      size: 9,
      number: 0,
      numberOfElements: 0,
      first: true,
      last: true,
      empty: true,
    });
  });
  it('should pause a vehicle ad', async () => {
    const resposta = {
      id: 1,
      statusVeiculo: 'PAUSADO',
    } as VeiculoResponse;

    const resultadoPromise = firstValueFrom(
      service.pausar(1)
    );

    const request = httpTesting.expectOne(
      `${environment.apiUrl}/veiculos/1/pausar`
    );

    expect(request.request.method).toBe('PATCH');
    expect(request.request.body).toBeNull();

    request.flush(resposta);

    expect(await resultadoPromise).toEqual(resposta);
  });

  it('should reactivate a vehicle ad', async () => {
    const resposta = {
      id: 1,
      statusVeiculo: 'DISPONIVEL',
    } as VeiculoResponse;

    const resultadoPromise = firstValueFrom(
      service.reativar(1)
    );

    const request = httpTesting.expectOne(
      `${environment.apiUrl}/veiculos/1/reativar`
    );

    expect(request.request.method).toBe('PATCH');
    expect(request.request.body).toBeNull();

    request.flush(resposta);

    expect(await resultadoPromise).toEqual(resposta);
  });
});