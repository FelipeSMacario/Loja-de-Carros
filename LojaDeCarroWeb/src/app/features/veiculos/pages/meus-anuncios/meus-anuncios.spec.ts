import { ComponentFixture, TestBed, } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { vi } from 'vitest';
import { provideRouter } from '@angular/router';
import { PageResponse } from '../../../../core/http/page-response';
import { VeiculoApi } from '../../data-access/veiculo-api';
import { VeiculoResponse } from '../../models/veiculo-response';
import { MeusAnuncios } from './meus-anuncios';
import { MatDialog } from '@angular/material/dialog';

describe('MeusAnuncios', () => {
  let component: MeusAnuncios;
  let fixture: ComponentFixture<MeusAnuncios>;

  const dialogMock = {
    open: vi.fn(),
  };

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

  const veiculoApiMock = {
    listarMeusAnuncios: vi.fn(),
    pausar: vi.fn(),
    reativar: vi.fn(),
  };


  beforeEach(async () => {
    dialogMock.open
      .mockReset()
      .mockReturnValue({
        afterClosed: () => of(true),
      });
    veiculoApiMock.listarMeusAnuncios
      .mockReset()
      .mockReturnValue(of(resposta));

    veiculoApiMock.pausar
      .mockReset()
      .mockReturnValue(of(veiculo));

    veiculoApiMock.reativar
      .mockReset()
      .mockReturnValue(of(veiculo));

    await TestBed.configureTestingModule({
      imports: [MeusAnuncios],
      providers: [
        provideRouter([]),
        {
          provide: VeiculoApi,
          useValue: veiculoApiMock,
        },
        {
          provide: MatDialog,
          useValue: dialogMock,
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(MeusAnuncios);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load the authenticated user ads', () => {
    expect(
      veiculoApiMock.listarMeusAnuncios
    ).toHaveBeenCalledWith(
      0,
      9,
      undefined
    );

    expect(component.veiculos()).toEqual([veiculo]);
    expect(component.totalElementos()).toBe(1);
    expect(component.carregando()).toBe(false);
    expect(component.erro()).toBe(false);
  });

  it('should filter ads by status', () => {
    veiculoApiMock.listarMeusAnuncios.mockClear();

    component.alterarStatus('PAUSADO');

    expect(component.statusSelecionado())
      .toBe('PAUSADO');

    expect(component.pagina()).toBe(0);

    expect(
      veiculoApiMock.listarMeusAnuncios
    ).toHaveBeenCalledWith(
      0,
      9,
      'PAUSADO'
    );
  });

  it('should load the selected page', () => {
    veiculoApiMock.listarMeusAnuncios.mockClear();

    component.alterarPagina({
      pageIndex: 1,
      pageSize: 6,
      length: 12,
      previousPageIndex: 0,
    });

    expect(component.pagina()).toBe(1);
    expect(component.tamanhoPagina()).toBe(6);

    expect(
      veiculoApiMock.listarMeusAnuncios
    ).toHaveBeenCalledWith(
      1,
      6,
      undefined
    );
  });

  it('should display an error when ads cannot be loaded', () => {
    veiculoApiMock.listarMeusAnuncios.mockReturnValue(
      throwError(() => new Error('Erro da API'))
    );

    component.carregar();

    expect(component.veiculos()).toEqual([]);
    expect(component.totalElementos()).toBe(0);
    expect(component.carregando()).toBe(false);
    expect(component.erro()).toBe(true);
  });
  it('should pause an available vehicle ad after confirmation', () => {
    veiculoApiMock.listarMeusAnuncios.mockClear();

    component.pausar(veiculo.id);

    expect(dialogMock.open)
      .toHaveBeenCalledOnce();

    expect(veiculoApiMock.pausar)
      .toHaveBeenCalledExactlyOnceWith(veiculo.id);

    expect(
      veiculoApiMock.listarMeusAnuncios
    ).toHaveBeenCalledWith(
      0,
      9,
      undefined
    );

    expect(component.acaoEmAndamentoId()).toBeNull();
    expect(component.erroAcao()).toBe(false);
  });

  it('should reactivate a paused vehicle ad', () => {
    veiculoApiMock.listarMeusAnuncios.mockClear();

    component.reativar(veiculo.id);

    expect(veiculoApiMock.reativar)
      .toHaveBeenCalledWith(veiculo.id);
      
    expect(dialogMock.open)
      .not.toHaveBeenCalled();
    expect(
      veiculoApiMock.listarMeusAnuncios
    ).toHaveBeenCalledWith(
      0,
      9,
      undefined
    );

    expect(component.acaoEmAndamentoId()).toBeNull();
    expect(component.erroAcao()).toBe(false);
  });

  it('should display an error when an ad action fails', () => {
    veiculoApiMock.listarMeusAnuncios.mockClear();

    veiculoApiMock.pausar.mockReturnValue(
      throwError(() => new Error('Erro da API'))
    );

    component.pausar(veiculo.id);

    expect(veiculoApiMock.pausar)
      .toHaveBeenCalledWith(veiculo.id);

    expect(
      veiculoApiMock.listarMeusAnuncios
    ).not.toHaveBeenCalled();

    expect(component.acaoEmAndamentoId()).toBeNull();
    expect(component.erroAcao()).toBe(true);
  });

  it('should not pause an ad when confirmation is cancelled', () => {
    veiculoApiMock.listarMeusAnuncios.mockClear();

    dialogMock.open.mockReturnValue({
      afterClosed: () => of(false),
    });

    component.pausar(veiculo.id);

    expect(dialogMock.open)
      .toHaveBeenCalledOnce();

    expect(veiculoApiMock.pausar)
      .not.toHaveBeenCalled();

    expect(veiculoApiMock.listarMeusAnuncios)
      .not.toHaveBeenCalled();

    expect(component.acaoEmAndamentoId()).toBeNull();
    expect(component.erroAcao()).toBe(false);
  });
});