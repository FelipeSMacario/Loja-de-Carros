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
import { MinhasCompras } from './minhas-compras';

describe('MinhasCompras', () => {
  let component: MinhasCompras;
  let fixture: ComponentFixture<MinhasCompras>;

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
    listarMinhasCompras: vi.fn(),
    cancelar: vi.fn(),
  };

  const dialogMock = {
    open: vi.fn(),
  };

  beforeEach(async () => {
    vendaApiMock.listarMinhasCompras
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

    dialogMock.open
      .mockReset()
      .mockReturnValue({
        afterClosed: () => of(true),
      });

    await TestBed.configureTestingModule({
      imports: [MinhasCompras],
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

    fixture = TestBed.createComponent(MinhasCompras);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load the authenticated user purchases', () => {
    expect(
      vendaApiMock.listarMinhasCompras
    ).toHaveBeenCalledExactlyOnceWith(
      0,
      9,
      undefined
    );

    expect(component.compras()).toEqual([venda]);
    expect(component.totalElementos()).toBe(1);
    expect(component.carregando()).toBe(false);
    expect(component.erro()).toBe(false);
  });

  it('should filter purchases by status', () => {
    vendaApiMock.listarMinhasCompras.mockClear();

    component.alterarStatus('CONCLUIDA');

    expect(component.statusSelecionado())
      .toBe('CONCLUIDA');

    expect(component.pagina()).toBe(0);

    expect(
      vendaApiMock.listarMinhasCompras
    ).toHaveBeenCalledExactlyOnceWith(
      0,
      9,
      'CONCLUIDA'
    );
  });

  it('should load the selected page', () => {
    vendaApiMock.listarMinhasCompras.mockClear();

    component.alterarPagina({
      pageIndex: 1,
      pageSize: 6,
      length: 12,
      previousPageIndex: 0,
    });

    expect(component.pagina()).toBe(1);
    expect(component.tamanhoPagina()).toBe(6);

    expect(
      vendaApiMock.listarMinhasCompras
    ).toHaveBeenCalledExactlyOnceWith(
      1,
      6,
      undefined
    );
  });

  it('should display an error when purchases cannot be loaded', () => {
    vendaApiMock.listarMinhasCompras.mockReturnValue(
      throwError(() => new Error('Erro da API'))
    );

    component.carregar();
    fixture.detectChanges();

    const element = fixture.nativeElement as HTMLElement;

    expect(component.compras()).toEqual([]);
    expect(component.totalElementos()).toBe(0);
    expect(component.carregando()).toBe(false);
    expect(component.erro()).toBe(true);

    expect(
      element.querySelector(
        '[data-testid="error-state"]'
      )
    ).not.toBeNull();
  });

  it('should cancel a purchase after confirmation', () => {
    vendaApiMock.listarMinhasCompras.mockClear();

    component.cancelar(venda.id);

    expect(dialogMock.open)
      .toHaveBeenCalledOnce();

    expect(vendaApiMock.cancelar)
      .toHaveBeenCalledExactlyOnceWith(venda.id);

    expect(
      vendaApiMock.listarMinhasCompras
    ).toHaveBeenCalledExactlyOnceWith(
      0,
      9,
      undefined
    );

    expect(component.acaoEmAndamentoId()).toBeNull();
    expect(component.erroAcao()).toBe(false);
  });

  it('should keep the purchase when confirmation is dismissed', () => {
    dialogMock.open.mockReturnValue({
      afterClosed: () => of(false),
    });

    component.cancelar(venda.id);

    expect(dialogMock.open)
      .toHaveBeenCalledOnce();

    expect(vendaApiMock.cancelar)
      .not.toHaveBeenCalled();
  });

  it('should display an error when cancellation fails', () => {
    vendaApiMock.cancelar.mockReturnValue(
      throwError(() => new Error('Erro da API'))
    );

    vendaApiMock.listarMinhasCompras.mockClear();

    component.cancelar(venda.id);
    fixture.detectChanges();

    const element = fixture.nativeElement as HTMLElement;

    expect(vendaApiMock.cancelar)
      .toHaveBeenCalledExactlyOnceWith(venda.id);

    expect(
      vendaApiMock.listarMinhasCompras
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