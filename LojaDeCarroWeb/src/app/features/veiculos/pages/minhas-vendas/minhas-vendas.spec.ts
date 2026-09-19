import {
  ComponentFixture,
  TestBed,
} from '@angular/core/testing';
import { MatDialog } from '@angular/material/dialog';
import { of, throwError } from 'rxjs';
import { vi } from 'vitest';

import { PageResponse } from '../../../../core/http/page-response';
import { VendaApi } from '../../data-access/venda-api';
import { VendaResponse } from '../../models/venda-response';
import { MinhasVendas } from './minhas-vendas';

describe('MinhasVendas', () => {
  let component: MinhasVendas;
  let fixture: ComponentFixture<MinhasVendas>;

  const venda: VendaResponse = {
    id: 7,
    valorVenda: 350000,
    statusVenda: 'EM_ANDAMENTO',
    dataVenda: '2026-09-19T12:00:00',
    veiculo: {
      id: 13,
      marca: 'Toyota',
      modelo: 'SW4',
      status: 'RESERVADO',
    },
    vendedor: {
      id: 2,
      nome: 'Felipe Vendedor',
    },
    comprador: {
      id: 3,
      nome: 'Steven Seagal',
    },
  };

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

  const vendaApiMock = {
    listarMinhasVendas: vi.fn(),
    cancelar: vi.fn(),
    concluir: vi.fn(),
  };

  const dialogMock = {
    open: vi.fn(),
  };

  beforeEach(async () => {
    vendaApiMock.listarMinhasVendas
      .mockReset()
      .mockReturnValue(of(resposta));

    vendaApiMock.cancelar
      .mockReset()
      .mockReturnValue(
        of({
          ...venda,
          statusVenda: 'CANCELADA',
        })
      );

    vendaApiMock.concluir
      .mockReset()
      .mockReturnValue(
        of({
          ...venda,
          statusVenda: 'CONCLUIDA',
        })
      );

    dialogMock.open
      .mockReset()
      .mockReturnValue({
        afterClosed: () => of(true),
      });

    await TestBed.configureTestingModule({
      imports: [MinhasVendas],
      providers: [
        {
          provide: VendaApi,
          useValue: vendaApiMock,
        },
        {
          provide: MatDialog,
          useValue: dialogMock,
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(MinhasVendas);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load the authenticated user sales', () => {
    expect(
      vendaApiMock.listarMinhasVendas
    ).toHaveBeenCalledExactlyOnceWith(
      0,
      9,
      undefined
    );

    expect(component.vendas()).toEqual([venda]);
    expect(component.totalElementos()).toBe(1);
    expect(component.carregando()).toBe(false);
    expect(component.erro()).toBe(false);
  });

  it('should filter sales by status', () => {
    vendaApiMock.listarMinhasVendas.mockClear();

    component.alterarStatus('EM_ANDAMENTO');

    expect(component.statusSelecionado())
      .toBe('EM_ANDAMENTO');

    expect(component.pagina()).toBe(0);

    expect(
      vendaApiMock.listarMinhasVendas
    ).toHaveBeenCalledExactlyOnceWith(
      0,
      9,
      'EM_ANDAMENTO'
    );
  });

  it('should load the selected page', () => {
    vendaApiMock.listarMinhasVendas.mockClear();

    component.alterarPagina({
      pageIndex: 1,
      pageSize: 6,
      length: 12,
      previousPageIndex: 0,
    });

    expect(component.pagina()).toBe(1);
    expect(component.tamanhoPagina()).toBe(6);

    expect(
      vendaApiMock.listarMinhasVendas
    ).toHaveBeenCalledExactlyOnceWith(
      1,
      6,
      undefined
    );
  });

  it('should display an error when sales cannot be loaded', () => {
    vendaApiMock.listarMinhasVendas.mockReturnValue(
      throwError(() => new Error('Erro da API'))
    );

    component.carregar();
    fixture.detectChanges();

    const element = fixture.nativeElement as HTMLElement;

    expect(component.vendas()).toEqual([]);
    expect(component.totalElementos()).toBe(0);
    expect(component.carregando()).toBe(false);
    expect(component.erro()).toBe(true);

    expect(
      element.querySelector(
        '[data-testid="error-state"]'
      )
    ).not.toBeNull();
  });

  it('should cancel a sale after confirmation', () => {
    vendaApiMock.listarMinhasVendas.mockClear();

    component.cancelar(venda.id);

    expect(dialogMock.open)
      .toHaveBeenCalledOnce();

    expect(vendaApiMock.cancelar)
      .toHaveBeenCalledExactlyOnceWith(venda.id);

    expect(
      vendaApiMock.listarMinhasVendas
    ).toHaveBeenCalledExactlyOnceWith(
      0,
      9,
      undefined
    );

    expect(component.acaoEmAndamentoId()).toBeNull();
    expect(component.erroAcao()).toBe(false);
  });

  it('should complete a sale after confirmation', () => {
    vendaApiMock.listarMinhasVendas.mockClear();

    component.concluir(venda.id);

    expect(dialogMock.open)
      .toHaveBeenCalledOnce();

    expect(vendaApiMock.concluir)
      .toHaveBeenCalledExactlyOnceWith(venda.id);

    expect(
      vendaApiMock.listarMinhasVendas
    ).toHaveBeenCalledExactlyOnceWith(
      0,
      9,
      undefined
    );

    expect(component.acaoEmAndamentoId()).toBeNull();
    expect(component.erroAcao()).toBe(false);
  });

  it('should keep the sale when confirmation is dismissed', () => {
    dialogMock.open.mockReturnValue({
      afterClosed: () => of(false),
    });

    component.concluir(venda.id);

    expect(dialogMock.open)
      .toHaveBeenCalledOnce();

    expect(vendaApiMock.concluir)
      .not.toHaveBeenCalled();
  });

  it('should display an error when a sale action fails', () => {
    vendaApiMock.concluir.mockReturnValue(
      throwError(() => new Error('Erro da API'))
    );

    vendaApiMock.listarMinhasVendas.mockClear();

    component.concluir(venda.id);
    fixture.detectChanges();

    const element = fixture.nativeElement as HTMLElement;

    expect(vendaApiMock.concluir)
      .toHaveBeenCalledExactlyOnceWith(venda.id);

    expect(
      vendaApiMock.listarMinhasVendas
    ).not.toHaveBeenCalled();

    expect(component.acaoEmAndamentoId()).toBeNull();
    expect(component.erroAcao()).toBe(true);

    expect(
      element.querySelector(
        '[data-testid="action-error"]'
      )
    ).not.toBeNull();
  });
});