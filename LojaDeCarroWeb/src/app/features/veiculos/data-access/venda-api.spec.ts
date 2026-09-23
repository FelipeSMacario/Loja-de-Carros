import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting, } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { firstValueFrom } from 'rxjs';
import { PageResponse } from '../../../core/http/page-response';
import { environment } from '../../../../environments/environment';
import { VendaResponse } from '../models/venda-response';
import { VendaApi } from './venda-api';

describe('VendaApi', () => {
  let api: VendaApi;
  let httpTesting: HttpTestingController;

  const venda: VendaResponse = {
    id: 7,
    valorVenda: 75990,
    statusVenda: 'EM_ANDAMENTO',
    dataVenda: '2026-09-18T12:00:00',
    veiculo: {
      id: 1,
      marca: 'Chevrolet',
      modelo: 'Onix',
      status: 'RESERVADO',
    },
    vendedor: {
      id: 2,
      nome: 'Vendedor',
    },
    comprador: {
      id: 3,
      nome: 'Comprador',
    },
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });

    api = TestBed.inject(VendaApi);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it('should create a sale for the selected vehicle', async () => {
    const resposta: VendaResponse = {
      id: 7,
      valorVenda: 75990,
      statusVenda: 'EM_ANDAMENTO',
      dataVenda: '2026-09-18T12:00:00',
      veiculo: {
        id: 1,
        marca: 'Chevrolet',
        modelo: 'Onix',
        status: 'RESERVADO',
      },
      vendedor: { id: 2, nome: 'Vendedor' },
      comprador: { id: 3, nome: 'Comprador' },
    };

    const resultado = firstValueFrom(
      api.criar({ veiculoId: 1 })
    );

    const request = httpTesting.expectOne(
      `${environment.apiUrl}/vendas`
    );

    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual({
      veiculoId: 1,
    });

    request.flush(resposta, {
      status: 201,
      statusText: 'Created',
    });

    expect(await resultado).toEqual(resposta);
  });
  it('should list the authenticated user purchases', async () => {
    const resposta: PageResponse<VendaResponse> = {
      content: [venda],
      totalElements: 1,
      totalPages: 1,
      size: 9,
      number: 0,
      numberOfElements: 1,
      first: true,
      last: true,
      empty: false,
    };

    const resultado = firstValueFrom(
      api.listarMinhasCompras(
        0,
        9,
        'EM_ANDAMENTO'
      )
    );

    const request = httpTesting.expectOne(
      `${environment.apiUrl}/vendas/minhas-compras`
      + '?page=0&size=9&status=EM_ANDAMENTO'
    );

    expect(request.request.method).toBe('GET');

    request.flush(resposta);

    expect(await resultado).toEqual(resposta);
  });

  it('should list the authenticated user sales', async () => {
    const resposta: PageResponse<VendaResponse> = {
      content: [venda],
      totalElements: 1,
      totalPages: 1,
      size: 6,
      number: 1,
      numberOfElements: 1,
      first: false,
      last: true,
      empty: false,
    };

    const resultado = firstValueFrom(
      api.listarMinhasVendas(1, 6)
    );

    const request = httpTesting.expectOne(
      `${environment.apiUrl}/vendas/minhas-vendas`
      + '?page=1&size=6'
    );

    expect(request.request.method).toBe('GET');

    request.flush(resposta);

    expect(await resultado).toEqual(resposta);
  });

  it('should cancel a sale', async () => {
    const vendaCancelada: VendaResponse = {
      ...venda,
      statusVenda: 'CANCELADA',
      veiculo: {
        ...venda.veiculo,
        status: 'DISPONIVEL',
      },
    };

    const resultado = firstValueFrom(
      api.cancelar(venda.id)
    );

    const request = httpTesting.expectOne(
      `${environment.apiUrl}/vendas/${venda.id}/cancelar`
    );

    expect(request.request.method).toBe('PATCH');
    expect(request.request.body).toBeNull();

    request.flush(vendaCancelada);

    expect(await resultado).toEqual(vendaCancelada);
  });

  it('should complete a sale', async () => {
    const vendaConcluida: VendaResponse = {
      ...venda,
      statusVenda: 'CONCLUIDA',
      veiculo: {
        ...venda.veiculo,
        status: 'VENDIDO',
      },
    };

    const resultado = firstValueFrom(
      api.concluir(venda.id)
    );

    const request = httpTesting.expectOne(
      `${environment.apiUrl}/vendas/${venda.id}/concluir`
    );

    expect(request.request.method).toBe('PATCH');
    expect(request.request.body).toBeNull();

    request.flush(vendaConcluida);

    expect(await resultado).toEqual(vendaConcluida);
  });
});