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

  beforeEach(async () => {
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
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({
                id: '1',
              }),
            },
          },
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
});