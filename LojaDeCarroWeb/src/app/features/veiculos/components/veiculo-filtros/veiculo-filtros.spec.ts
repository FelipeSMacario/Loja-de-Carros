import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { vi } from 'vitest';

import { VeiculoCatalogoApi } from '../../data-access/veiculo-catalogo-api';
import { VeiculoFiltroCatalogos } from '../../models/veiculo-catalogos';
import { VeiculoFiltros } from './veiculo-filtros';

describe('VeiculoFiltros', () => {
  let component: VeiculoFiltros;
  let fixture: ComponentFixture<VeiculoFiltros>;

  const catalogos: VeiculoFiltroCatalogos = {
    carrocerias: [
      {
        id: 1,
        nome: 'Hatch',
        ativo: true,
      },
    ],
    combustiveis: [
      {
        id: 1,
        nome: 'Flex',
        ativo: true,
      },
    ],
    cores: [
      {
        id: 1,
        nome: 'Branco',
        ativo: true,
      },
    ],
    modelos: [
      {
        id: 10,
        nome: 'Onix',
        ativo: true,
        marca: {
          id: 1,
          nome: 'Chevrolet',
          ativo: true,
        },
      },
      {
        id: 11,
        nome: 'Tracker',
        ativo: true,
        marca: {
          id: 1,
          nome: 'Chevrolet',
          ativo: true,
        },
      },
      {
        id: 20,
        nome: 'Mustang',
        ativo: true,
        marca: {
          id: 2,
          nome: 'Ford',
          ativo: true,
        },
      },
    ],
  };

  const catalogoApiMock = {
    carregarFiltros: vi.fn(),
  };

  beforeEach(async () => {
    vi.clearAllMocks();

    catalogoApiMock.carregarFiltros.mockReturnValue(of(catalogos));

    await TestBed.configureTestingModule({
      imports: [VeiculoFiltros],
      providers: [
        {
          provide: VeiculoCatalogoApi,
          useValue: catalogoApiMock,
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(VeiculoFiltros);
    component = fixture.componentInstance;
  });

  it('should create and load filter catalogs', () => {
    fixture.detectChanges();

    expect(component).toBeTruthy();

    expect(catalogoApiMock.carregarFiltros).toHaveBeenCalledOnce();

    expect(component.catalogos()).toEqual(catalogos);
    expect(component.carregandoCatalogos()).toBe(false);
  });

  it('should extract brands without duplicates', () => {
    fixture.detectChanges();

    expect(component.marcas()).toEqual([
      {
        id: 1,
        nome: 'Chevrolet',
        ativo: true,
      },
      {
        id: 2,
        nome: 'Ford',
        ativo: true,
      },
    ]);
  });

  it('should show only models from the selected brand', () => {
    fixture.detectChanges();

    component.formulario.controls.marcaId.setValue(1);

    expect(component.modelosFiltrados().map((modelo) => modelo.nome)).toEqual(['Onix', 'Tracker']);
  });

  it('should clear the model when the brand changes', () => {
    fixture.detectChanges();

    component.formulario.controls.marcaId.setValue(1);
    component.formulario.controls.modeloId.setValue(10);

    component.formulario.controls.marcaId.setValue(2);

    expect(component.formulario.controls.modeloId.value).toBeNull();
  });

  it('should emit only populated filters', () => {
    fixture.detectChanges();

    const emitirFiltro = vi.fn();

    component.filtroAplicado.subscribe(emitirFiltro);

    component.formulario.patchValue({
      marcaId: 1,
      anoMin: 2020,
      anoMax: null,
      valorMin: null,
      valorMax: 100000,
    });

    component.aplicarFiltros();

    expect(emitirFiltro).toHaveBeenCalledExactlyOnceWith({
      marcaId: 1,
      anoMin: 2020,
      valorMax: 100000,
    });
  });

  it('should not emit when the year range is invalid', () => {
    fixture.detectChanges();

    const emitirFiltro = vi.fn();

    component.filtroAplicado.subscribe(emitirFiltro);

    component.formulario.patchValue({
      anoMin: 2025,
      anoMax: 2020,
    });

    component.aplicarFiltros();

    expect(component.formulario.invalid).toBe(true);

    expect(component.formulario.hasError('intervaloAnoInvalido')).toBe(true);

    expect(emitirFiltro).not.toHaveBeenCalled();
  });

  it('should not emit when the price range is invalid', () => {
    fixture.detectChanges();

    const emitirFiltro = vi.fn();

    component.filtroAplicado.subscribe(emitirFiltro);

    component.formulario.patchValue({
      valorMin: 150000,
      valorMax: 50000,
    });

    component.aplicarFiltros();

    expect(component.formulario.invalid).toBe(true);

    expect(component.formulario.hasError('intervaloValorInvalido')).toBe(true);

    expect(emitirFiltro).not.toHaveBeenCalled();
  });

  it('should clear the form and emit an empty filter', () => {
    fixture.detectChanges();

    const emitirFiltro = vi.fn();

    component.filtroAplicado.subscribe(emitirFiltro);

    component.formulario.patchValue({
      marcaId: 1,
      modeloId: 10,
      anoMin: 2020,
      valorMax: 100000,
    });

    component.limparFiltros();

    expect(component.formulario.getRawValue()).toEqual({
      marcaId: null,
      modeloId: null,
      carroceriaId: null,
      corId: null,
      combustivelId: null,
      anoMin: null,
      anoMax: null,
      valorMin: null,
      valorMax: null,
      quilometragemMax: null,
    });

    expect(emitirFiltro).toHaveBeenCalledExactlyOnceWith({});
  });
});
