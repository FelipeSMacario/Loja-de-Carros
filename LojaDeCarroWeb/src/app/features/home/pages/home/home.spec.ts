import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { vi } from 'vitest';
import {
  MatPaginatorModule,
  PageEvent,
} from '@angular/material/paginator';
import { VeiculoApi } from '../../../veiculos/data-access/veiculo-api';
import { Home } from './home';
import { provideRouter } from '@angular/router';


describe('Home', () => {
  let fixture: ComponentFixture<Home>;
  let component: Home;

  const veiculoApiMock = {
    listarAtivos: vi.fn(),
  };

  const resposta = {
    content: [
      {
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
        statusVeiculo: 'DISPONIVEL' as const,
        imagemPrincipalId: 10,
      },
    ],
    totalElements: 1,
    totalPages: 1,
    size: 9,
    number: 0,
    numberOfElements: 1,
    first: true,
    last: true,
    empty: false,
  };

  beforeEach(async () => {
    vi.clearAllMocks();

    veiculoApiMock.listarAtivos.mockReturnValue(
      of(resposta)
    );

    await TestBed.configureTestingModule({
      imports: [Home],
      providers: [
        provideRouter([]),
        {
          provide: VeiculoApi,
          useValue: veiculoApiMock,
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(Home);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    fixture.detectChanges();

    expect(component).toBeTruthy();
  });

  it('should load and display active vehicles', () => {
    fixture.detectChanges();

    const element = fixture.nativeElement as HTMLElement;

    expect(veiculoApiMock.listarAtivos)
      .toHaveBeenCalledExactlyOnceWith(0, 9);

    expect(element.textContent).toContain('Onix');
    expect(element.textContent).toContain('Chevrolet');
  });

  it('should display an empty state when there are no vehicles', () => {
    veiculoApiMock.listarAtivos.mockReturnValue(
      of({
        ...resposta,
        content: [],
        totalElements: 0,
        numberOfElements: 0,
        empty: true,
      })
    );

    fixture.detectChanges();

    const element = fixture.nativeElement as HTMLElement;

    expect(element.textContent)
      .toContain('Nenhum veículo disponível');

    expect(
      element.querySelector('app-veiculo-card')
    ).toBeNull();
  });

  it('should display an error when vehicles cannot be loaded', () => {
    veiculoApiMock.listarAtivos.mockReturnValue(
      throwError(() => new Error('API indisponível'))
    );

    fixture.detectChanges();

    const element = fixture.nativeElement as HTMLElement;

    expect(element.textContent)
      .toContain('Não foi possível carregar os veículos');

    expect(
      element.querySelector('[role="alert"]')
    ).not.toBeNull();
  });

  it('should load the page selected by the user', () => {
    fixture.detectChanges();

    const evento: PageEvent = {
      pageIndex: 1,
      pageSize: 9,
      length: 20,
      previousPageIndex: 0,
    };

    component.alterarPagina(evento);

    expect(veiculoApiMock.listarAtivos)
      .toHaveBeenLastCalledWith(1, 9);
  });
});