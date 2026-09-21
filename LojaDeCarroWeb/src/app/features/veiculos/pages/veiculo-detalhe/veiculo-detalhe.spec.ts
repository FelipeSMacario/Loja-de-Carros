import { ComponentFixture, TestBed, } from '@angular/core/testing';
import {
  ActivatedRoute,
  convertToParamMap,
  provideRouter,
  Router,
} from '@angular/router';
import { of, throwError } from 'rxjs';
import { VeiculoApi } from '../../data-access/veiculo-api';
import { VeiculoDetalheResponse } from '../../models/veiculo-detalhe-response';
import { VeiculoDetalhe } from './veiculo-detalhe';
import { signal } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { AuthService } from '../../../../core/auth/auth-service';
import { VendaApi } from '../../data-access/venda-api';
import { UsuarioApi } from '../../../usuarios/data-access/usuario-api';
import { HttpErrorResponse } from '@angular/common/http';

describe('VeiculoDetalhe', () => {
  let component: VeiculoDetalhe;
  let fixture: ComponentFixture<VeiculoDetalhe>;
  let router: Router;
  let navigateMock: ReturnType<typeof vi.spyOn>;

  const veiculoApiMock = {
    buscarPorId: vi.fn(),
    buscarMeuAnuncio: vi.fn(),
  };

  const autenticado = signal(false);
  const inicializado = signal(true);

  const authServiceMock = {
    autenticado,
    inicializado,
    possuiRole: vi.fn().mockReturnValue(true),
    entrar: vi.fn().mockResolvedValue(undefined),
  };

  const usuarioApiMock = {
    buscarAtual: vi.fn(),
  };

  const vendaApiMock = {
    criar: vi.fn(),
  };

  const dialogMock = {
    open: vi.fn(),
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
      paramMap: convertToParamMap({ id: '1' }),
      data: {} as Record<string, unknown>,
    },
  };

  beforeEach(async () => {
    activatedRouteMock.snapshot.data = {};
    vi.clearAllMocks();

    autenticado.set(false);
    inicializado.set(true);

    usuarioApiMock.buscarAtual.mockReset();
    vendaApiMock.criar.mockReset();
    dialogMock.open.mockReset();
    authServiceMock.possuiRole.mockReturnValue(true);

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
        {
          provide: AuthService,
          useValue: authServiceMock,
        },
        {
          provide: UsuarioApi,
          useValue: usuarioApiMock,
        },
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
    router = TestBed.inject(Router);

    navigateMock = vi
      .spyOn(router, 'navigate')
      .mockResolvedValue(true);
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
  it('should send a visitor to login before purchasing', () => {
    veiculoApiMock.buscarPorId.mockReturnValue(of(veiculo));

    criarComponente();
    component.iniciarCompra();

    expect(authServiceMock.entrar).toHaveBeenCalledOnce();
    expect(dialogMock.open).not.toHaveBeenCalled();
    expect(vendaApiMock.criar).not.toHaveBeenCalled();
  });

  it('should hide the purchase action from the seller', () => {
    autenticado.set(true);
    usuarioApiMock.buscarAtual.mockReturnValue(
      of({ id: veiculo.vendedor.id })
    );
    veiculoApiMock.buscarPorId.mockReturnValue(of(veiculo));

    criarComponente();

    const element = fixture.nativeElement as HTMLElement;

    expect(usuarioApiMock.buscarAtual)
      .toHaveBeenCalledOnce();

    expect(
      element.querySelector(
        '[data-testid="start-purchase-button"]'
      )
    ).toBeNull();

    expect(component.podeIniciarCompra()).toBe(false);
  });

  it('should not create a sale when confirmation is cancelled', () => {
    autenticado.set(true);
    usuarioApiMock.buscarAtual.mockReturnValue(
      of({ id: 6 })
    );
    veiculoApiMock.buscarPorId.mockReturnValue(of(veiculo));
    dialogMock.open.mockReturnValue({
      afterClosed: () => of(false),
    });

    criarComponente();
    component.iniciarCompra();

    expect(dialogMock.open).toHaveBeenCalledOnce();
    expect(vendaApiMock.criar).not.toHaveBeenCalled();
    expect(component.vendaCriada()).toBeNull();
  });

  it('should reserve the vehicle after confirming the purchase', () => {
    autenticado.set(true);
    usuarioApiMock.buscarAtual.mockReturnValue(
      of({ id: 6 })
    );
    veiculoApiMock.buscarPorId.mockReturnValue(of(veiculo));
    dialogMock.open.mockReturnValue({
      afterClosed: () => of(true),
    });
    vendaApiMock.criar.mockReturnValue(
      of({
        id: 7,
        veiculo: {
          id: veiculo.id,
          status: 'RESERVADO',
        },
      })
    );

    criarComponente();
    component.iniciarCompra();
    fixture.detectChanges();

    expect(vendaApiMock.criar)
      .toHaveBeenCalledExactlyOnceWith({
        veiculoId: 1,
      });

    expect(component.vendaCriada()?.id).toBe(7);
    expect(component.veiculo()?.statusVeiculo)
      .toBe('RESERVADO');

    const element = fixture.nativeElement as HTMLElement;

    expect(element.textContent)
      .toContain('Compra iniciada!');

    expect(
      element.querySelector(
        '[data-testid="start-purchase-button"]'
      )
    ).toBeNull();
  });
  it('should redirect a user without local profile to registration', () => {
    autenticado.set(true);

    usuarioApiMock.buscarAtual.mockReturnValue(
      throwError(() =>
        new HttpErrorResponse({
          status: 403,
          statusText: 'Forbidden',
          error: {
            message:
              'Usuário autenticado não possui cadastro local.',
          },
        })
      )
    );

    veiculoApiMock.buscarPorId.mockReturnValue(
      of(veiculo)
    );

    criarComponente();

    expect(usuarioApiMock.buscarAtual)
      .toHaveBeenCalledOnce();

    expect(navigateMock)
      .toHaveBeenCalledExactlyOnceWith(
        ['/completar-cadastro'],
        {
          queryParams: {
            returnUrl: '/veiculos/1',
          },
        }
      );

    expect(component.erroUsuario()).toBe(false);
  });
});