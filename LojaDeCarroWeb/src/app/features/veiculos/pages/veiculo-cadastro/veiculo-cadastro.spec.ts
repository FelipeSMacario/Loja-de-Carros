import { ComponentFixture, TestBed, } from '@angular/core/testing';
import { of, throwError, Subject, } from 'rxjs';
import { vi } from 'vitest';
import { VeiculoCatalogoApi } from '../../data-access/veiculo-catalogo-api';
import { VeiculoCatalogos } from '../../models/veiculo-catalogos';
import { VeiculoCadastro } from './veiculo-cadastro';
import { ActivatedRoute, convertToParamMap, Router, } from '@angular/router';
import { VeiculoApi } from '../../data-access/veiculo-api';
import { VeiculoEdicaoResponse } from '../../models/veiculo-edicao-response';

describe('VeiculoCadastro', () => {
  let component: VeiculoCadastro;
  let fixture: ComponentFixture<VeiculoCadastro>;

  function preencherFormularioValido(): void {
    component.formulario.setValue({
      placa: 'ABC1D23',
      anoFabricacao: 2024,
      quilometragem: 28000,
      valor: 350000,
      motor: '2.0',
      descricao: 'Veículo em ótimo estado',
      idModelo: 4,
      idCarroceria: 1,
      idCor: 3,
      idCombustivel: 2,
      idsOpcionais: [],
    });
  }

  function criarEventoComArquivos(
    arquivos: File[]
  ): Event {
    return {
      target: {
        files: arquivos,
      },
    } as unknown as Event;
  }

  const veiculoApiMock = {
    criar: vi.fn(),
    atualizar: vi.fn(),
    buscarMeuAnuncioParaEdicao: vi.fn(),
  };

  const routerMock = {
    navigate: vi.fn(),
  };

  const activatedRouteMock = {
    snapshot: {
      data: {} as Record<string, unknown>,
      paramMap: convertToParamMap({}),
    },
  };

  const catalogos: VeiculoCatalogos = {
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
  };

  const veiculoParaEdicao: VeiculoEdicaoResponse = {
    id: 42,
    placa: 'ABC1D23',
    anoFabricacao: 2024,
    quilometragem: 28000,
    valor: 350000,
    motor: '2.0',
    descricao: null,
    idModelo: 4,
    idCarroceria: 1,
    idCor: 3,
    idCombustivel: 2,
    idsOpcionais: [6],
    statusVeiculo: 'PAUSADO',
    imagens: [
      {
        id: 10,
        principal: true,
      },
    ],
  };

  const catalogoApiMock = {
    carregar: vi.fn(),
  };

  beforeEach(async () => {
    activatedRouteMock.snapshot.data = {};
    activatedRouteMock.snapshot.paramMap =
      convertToParamMap({});

    veiculoApiMock.criar.mockReset();
    routerMock.navigate.mockReset();

    veiculoApiMock.criar.mockReturnValue(
      of({ id: 99 })
    );
    catalogoApiMock.carregar.mockReset();
    catalogoApiMock.carregar.mockReturnValue(
      of(catalogos)
    );
    veiculoApiMock.atualizar
      .mockReset()
      .mockReturnValue(of({ id: 99 }));

    veiculoApiMock.buscarMeuAnuncioParaEdicao
      .mockReset();

    await TestBed.configureTestingModule({
      imports: [VeiculoCadastro],
      providers: [
        {
          provide: VeiculoCatalogoApi,
          useValue: catalogoApiMock,
        },
        {
          provide: VeiculoApi,
          useValue: veiculoApiMock,
        },
        {
          provide: Router,
          useValue: routerMock,
        },
        {
          provide: ActivatedRoute,
          useValue: activatedRouteMock,
        },
      ],
    }).compileComponents();
  });

  function criarComponente(): void {
    fixture = TestBed.createComponent(VeiculoCadastro);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }

  it('should create', () => {
    criarComponente();

    expect(component).toBeTruthy();
  });

  it('should load vehicle catalogs', () => {
    criarComponente();

    expect(catalogoApiMock.carregar)
      .toHaveBeenCalledOnce();

    expect(component.catalogos()).toEqual(catalogos);
    expect(component.carregandoCatalogos()).toBe(false);
    expect(component.erroCatalogos()).toBe(false);
  });

  it('should display catalog loading failure', () => {
    catalogoApiMock.carregar.mockReturnValue(
      throwError(
        () => new Error('Falha ao carregar catálogos')
      )
    );

    criarComponente();

    expect(component.catalogos()).toBeNull();
    expect(component.carregandoCatalogos()).toBe(false);
    expect(component.erroCatalogos()).toBe(true);
  });

  it('should allow an empty optional list', () => {
    criarComponente();

    expect(
      component.formulario.controls
        .idsOpcionais.value
    ).toEqual([]);

    expect(
      component.formulario.controls
        .idsOpcionais.valid
    ).toBe(true);
  });

  it('should validate the vehicle form', () => {
    criarComponente();

    component.formulario.setValue({
      placa: 'ABC1D23',
      anoFabricacao: 2024,
      quilometragem: 28000,
      valor: 350000,
      motor: '2.0',
      descricao: 'Veículo em ótimo estado',
      idModelo: 4,
      idCarroceria: 1,
      idCor: 3,
      idCombustivel: 2,
      idsOpcionais: [],
    });

    expect(component.formulario.valid).toBe(true);
  });
  it('should create a vehicle and navigate to its details', () => {
    criarComponente();

    component.formulario.setValue({
      placa: 'ABC1D23',
      anoFabricacao: 2024,
      quilometragem: 28000,
      valor: 350000,
      motor: '2.0',
      descricao: 'Veículo em ótimo estado',
      idModelo: 4,
      idCarroceria: 1,
      idCor: 3,
      idCombustivel: 2,
      idsOpcionais: [],
    });

    component.salvar();

    expect(veiculoApiMock.criar)
      .toHaveBeenCalledExactlyOnceWith(
        component.formulario.getRawValue(),
        []
      );

    expect(routerMock.navigate)
      .toHaveBeenCalledExactlyOnceWith([
        '/veiculos',
        99,
      ]);
  });
  it('should not create an invalid vehicle', () => {
    criarComponente();

    component.salvar();

    expect(veiculoApiMock.criar)
      .not.toHaveBeenCalled();

    expect(component.formulario.touched).toBe(true);
  });

  it('should select and remove vehicle images', () => {
    criarComponente();

    const arquivos = [
      new File(['frente'], 'frente.jpg', {
        type: 'image/jpeg',
      }),
      new File(['traseira'], 'traseira.jpg', {
        type: 'image/jpeg',
      }),
    ];

    component.selecionarArquivos(
      criarEventoComArquivos(arquivos)
    );

    expect(component.arquivosSelecionados())
      .toEqual(arquivos);

    component.removerArquivo(0);

    expect(
      component.arquivosSelecionados()
        .map(arquivo => arquivo.name)
    ).toEqual(['traseira.jpg']);
  });

  it('should limit the vehicle to ten images', () => {
    criarComponente();

    const arquivos = Array.from(
      { length: 11 },
      (_valor, indice) =>
        new File(
          [`imagem-${indice}`],
          `imagem-${indice}.jpg`,
          { type: 'image/jpeg' }
        )
    );

    component.selecionarArquivos(
      criarEventoComArquivos(arquivos)
    );

    expect(component.arquivosSelecionados())
      .toHaveLength(10);

    expect(component.erroArquivos())
      .toContain('no máximo 10 imagens');
  });

  it('should display an error when creation fails', () => {
    veiculoApiMock.criar.mockReturnValue(
      throwError(
        () => new Error('Falha no cadastro')
      )
    );

    criarComponente();
    preencherFormularioValido();

    component.salvar();

    expect(component.erroCadastro()).toBe(true);
    expect(component.enviando()).toBe(false);

    expect(routerMock.navigate)
      .not.toHaveBeenCalled();
  });

  it('should prevent duplicate submissions', () => {
    const resposta$ = new Subject<{ id: number }>();

    veiculoApiMock.criar.mockReturnValue(
      resposta$
    );

    criarComponente();
    preencherFormularioValido();

    component.salvar();
    component.salvar();

    expect(veiculoApiMock.criar)
      .toHaveBeenCalledOnce();

    expect(component.enviando()).toBe(true);

    resposta$.next({ id: 99 });
    resposta$.complete();

    expect(component.enviando()).toBe(false);

    expect(routerMock.navigate)
      .toHaveBeenCalledExactlyOnceWith([
        '/veiculos',
        99,
      ]);
  });
  it('should load an ad for editing', () => {
    activatedRouteMock.snapshot.data = {
      modoEdicao: true,
    };

    activatedRouteMock.snapshot.paramMap =
      convertToParamMap({
        id: '42',
      });

    veiculoApiMock.buscarMeuAnuncioParaEdicao
      .mockReturnValue(of(veiculoParaEdicao));

    criarComponente();

    expect(
      veiculoApiMock.buscarMeuAnuncioParaEdicao
    ).toHaveBeenCalledExactlyOnceWith(42);

    expect(component.modoEdicao).toBe(true);
    expect(component.tituloPagina).toBe('Editar anúncio');

    expect(component.formulario.getRawValue())
      .toEqual({
        placa: 'ABC1D23',
        anoFabricacao: 2024,
        quilometragem: 28000,
        valor: 350000,
        motor: '2.0',
        descricao: '',
        idModelo: 4,
        idCarroceria: 1,
        idCor: 3,
        idCombustivel: 2,
        idsOpcionais: [6],
      });

    expect(component.imagensExistentes())
      .toEqual(veiculoParaEdicao.imagens);

    expect(component.carregandoVeiculo())
      .toBe(false);

    expect(component.erroCarregamentoVeiculo())
      .toBe(false);
  });

  it('should update an ad and navigate to its owner details', () => {
    activatedRouteMock.snapshot.data = {
      modoEdicao: true,
    };

    activatedRouteMock.snapshot.paramMap =
      convertToParamMap({
        id: '42',
      });

    veiculoApiMock.buscarMeuAnuncioParaEdicao
      .mockReturnValue(of(veiculoParaEdicao));

    veiculoApiMock.atualizar.mockReturnValue(
      of({
        id: 42,
      })
    );

    criarComponente();

    component.formulario.controls.descricao.setValue(
      'Veículo em ótimo estado'
    );

    component.salvar();

    expect(veiculoApiMock.atualizar)
      .toHaveBeenCalledExactlyOnceWith(
        42,
        component.formulario.getRawValue()
      );

    expect(veiculoApiMock.criar)
      .not.toHaveBeenCalled();

    expect(routerMock.navigate)
      .toHaveBeenCalledExactlyOnceWith([
        '/veiculos/meus-anuncios',
        42,
      ]);
  });

  it('should display an error when the ad cannot be loaded for editing', () => {
    activatedRouteMock.snapshot.data = {
      modoEdicao: true,
    };

    activatedRouteMock.snapshot.paramMap =
      convertToParamMap({
        id: '42',
      });

    veiculoApiMock.buscarMeuAnuncioParaEdicao
      .mockReturnValue(
        throwError(
          () => new Error('Falha ao carregar anúncio')
        )
      );

    criarComponente();

    expect(component.carregandoVeiculo())
      .toBe(false);

    expect(component.erroCarregamentoVeiculo())
      .toBe(true);

    expect(veiculoApiMock.atualizar)
      .not.toHaveBeenCalled();
  });
});