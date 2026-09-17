import { ComponentFixture, TestBed, } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap, } from '@angular/router';
import { of, throwError } from 'rxjs';
import { VeiculoApi } from '../../data-access/veiculo-api';
import { VeiculoDetalheResponse } from '../../models/veiculo-detalhe-response';
import { VeiculoDetalhe } from './veiculo-detalhe';
import { provideRouter } from '@angular/router';

describe('VeiculoDetalhe', () => {
  let component: VeiculoDetalhe;
  let fixture: ComponentFixture<VeiculoDetalhe>;

  const veiculoApiMock = {
    buscarPorId: vi.fn(),
    buscarMeuAnuncio: vi.fn(),
  };

  const veiculo: VeiculoDetalheResponse = {
    id: 1,
    marca: 'Peugeot',
    modelo: 'Pré-explosão',
    carroceria: 'Hatch',
    cor: 'Cinza',
    combustivel: 'Gasolina',
    motor: '1.6',
    valor: 35000,
    quilometragem: 90000,
    anoFabricacao: 2018,
    statusVeiculo: 'DISPONIVEL',
    descricao: 'Perfeito estado, segundo o vendedor.',
    dataCadastro: '2026-09-12T11:30:00',
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
    ],
  };
  const activatedRouteMock = {
    snapshot: {
      paramMap: convertToParamMap({
        id: '1',
      }),
      data: {} as Record<string, unknown>,
    },
  };
  beforeEach(async () => {
    activatedRouteMock.snapshot.data = {};
    vi.clearAllMocks();

    await TestBed.configureTestingModule({
      imports: [VeiculoDetalhe],
      providers: [
        provideRouter([]),
        {
          provide: VeiculoApi,
          useValue: veiculoApiMock,
        },
        {
          provide: ActivatedRoute,
          useValue: activatedRouteMock,
        },
      ],
    }).compileComponents();
  });

  function criarComponente(): void {
    fixture = TestBed.createComponent(VeiculoDetalhe);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }

  it('should create', () => {
    veiculoApiMock.buscarPorId.mockReturnValue(
      of(veiculo)
    );

    criarComponente();

    expect(component).toBeTruthy();
  });

  it('should load and display vehicle details', () => {
    veiculoApiMock.buscarPorId.mockReturnValue(
      of(veiculo)
    );

    criarComponente();

    const element = fixture.nativeElement as HTMLElement;

    expect(veiculoApiMock.buscarPorId)
      .toHaveBeenCalledExactlyOnceWith(1);

    expect(
      element.querySelector(
        '[data-testid="vehicle-title"]'
      )?.textContent
    ).toContain('Pré-explosão');

    expect(element.textContent)
      .toContain('Peugeot');

    expect(element.textContent)
      .toContain('Perfeito estado');
    expect(
      element.querySelector(
        '[data-testid="edit-ad-link"]'
      )
    ).toBeNull();
  });

  it('should display an error when the vehicle cannot be loaded', () => {
    veiculoApiMock.buscarPorId.mockReturnValue(
      throwError(() => new Error('API indisponível'))
    );

    criarComponente();

    const element = fixture.nativeElement as HTMLElement;

    expect(
      element.querySelector(
        '[data-testid="vehicle-error"]'
      )
    ).not.toBeNull();

    expect(element.textContent)
      .toContain('Não foi possível carregar o veículo');

    expect(component.imagemSelecionadaId())
      .toBeNull();
  });
  it('should select another gallery image', () => {
    veiculoApiMock.buscarPorId.mockReturnValue(
      of(veiculo)
    );

    criarComponente();

    component.selecionarImagem(11);

    expect(component.imagemSelecionadaId())
      .toBe(11);

    expect(component.imagemSelecionadaUrl())
      .toContain('/imagens/11/conteudo');
  });
  it('should load an authenticated user sold ad', () => {
    const anuncioVendido: VeiculoDetalheResponse = {
      ...veiculo,
      statusVeiculo: 'VENDIDO',
    };

    activatedRouteMock.snapshot.data = {
      meuAnuncio: true,
    };

    veiculoApiMock.buscarMeuAnuncio.mockReturnValue(
      of(anuncioVendido)
    );

    criarComponente();

    const element = fixture.nativeElement as HTMLElement;

    expect(element.textContent)
      .toContain('Vendido');

    expect(
      element.querySelector(
        '[data-testid="edit-ad-link"]'
      )
    ).toBeNull();

    expect(veiculoApiMock.buscarMeuAnuncio)
      .toHaveBeenCalledExactlyOnceWith(1);

    expect(veiculoApiMock.buscarPorId)
      .not.toHaveBeenCalled();

    expect(component.gerenciandoMeuAnuncio)
      .toBe(true);

    expect(component.rotaRetorno)
      .toBe('/veiculos/meus-anuncios');

    expect(component.textoRetorno)
      .toBe('Voltar aos meus anúncios');

    expect(component.veiculo()?.statusVeiculo)
      .toBe('VENDIDO');
  });

  it('should allow the owner to edit a paused ad', () => {
    activatedRouteMock.snapshot.data = {
      meuAnuncio: true,
    };

    const anuncioPausado: VeiculoDetalheResponse = {
      ...veiculo,
      statusVeiculo: 'PAUSADO',
    };

    veiculoApiMock.buscarMeuAnuncio.mockReturnValue(
      of(anuncioPausado)
    );

    criarComponente();

    const element = fixture.nativeElement as HTMLElement;

    const link = element.querySelector(
      '[data-testid="edit-ad-link"]'
    );

    expect(element.textContent)
      .toContain('Pausado');

    expect(link?.getAttribute('href'))
      .toBe('/veiculos/meus-anuncios/1/editar');
  });
});