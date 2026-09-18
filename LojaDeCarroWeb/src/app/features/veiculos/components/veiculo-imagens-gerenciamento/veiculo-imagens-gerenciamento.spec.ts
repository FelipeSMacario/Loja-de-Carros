import { ComponentFixture, TestBed, } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { vi } from 'vitest';
import { ImagemApi } from '../../data-access/imagem-api';
import { ImagemResponse } from '../../models/imagem-response';
import { VeiculoImagensGerenciamento } from './veiculo-imagens-gerenciamento';
import { MatDialog } from '@angular/material/dialog';

describe('VeiculoImagensGerenciamento', () => {
  let component: VeiculoImagensGerenciamento;
  let fixture: ComponentFixture<VeiculoImagensGerenciamento>;

  const imagemApiMock = {
    listarPorVeiculo: vi.fn(),
    adicionarAoVeiculo: vi.fn(),
    excluir: vi.fn(),
    definirPrincipal: vi.fn(),
  };

  const dialogMock = {
    open: vi.fn(),
  };

  const imagens: ImagemResponse[] = [
    {
      id: 2,
      nomeOriginal: 'traseira.jpg',
      objectKey: 'veiculos/1/traseira.jpg',
      principal: false,
    },
    {
      id: 1,
      nomeOriginal: 'principal.jpg',
      objectKey: 'veiculos/1/principal.jpg',
      principal: true,
    },
  ];

  beforeEach(async () => {
    vi.clearAllMocks();

    dialogMock.open.mockReturnValue({
      afterClosed: () => of(true),
    });

    await TestBed.configureTestingModule({
      imports: [VeiculoImagensGerenciamento],
      providers: [
        {
          provide: ImagemApi,
          useValue: imagemApiMock,
        },
        {
          provide: MatDialog,
          useValue: dialogMock,
        },
      ],
    }).compileComponents();
  });

  function criarComponente(): void {
    fixture = TestBed.createComponent(
      VeiculoImagensGerenciamento
    );
    component = fixture.componentInstance;

    fixture.componentRef.setInput('idVeiculo', 1);
    fixture.detectChanges();
  }

  it('should create', () => {
    imagemApiMock.listarPorVeiculo.mockReturnValue(
      of([])
    );

    criarComponente();

    expect(component).toBeTruthy();
  });

  it('should load vehicle images with the main image first', () => {
    imagemApiMock.listarPorVeiculo.mockReturnValue(
      of(imagens)
    );

    criarComponente();

    expect(imagemApiMock.listarPorVeiculo)
      .toHaveBeenCalledExactlyOnceWith(1);

    expect(
      component.imagens().map(imagem => imagem.id)
    ).toEqual([1, 2]);

    expect(component.carregando()).toBe(false);
    expect(component.erro()).toBe(false);
    expect(component.quantidadeDisponivel()).toBe(8);
    expect(component.podeAdicionar()).toBe(true);

    expect(component.imagemUrl(1))
      .toContain('/imagens/1/conteudo');
  });

  it('should display an error when images cannot be loaded', () => {
    imagemApiMock.listarPorVeiculo.mockReturnValue(
      throwError(() => new Error('API indisponível'))
    );

    criarComponente();

    expect(component.imagens()).toEqual([]);
    expect(component.carregando()).toBe(false);
    expect(component.erro()).toBe(true);
  });

  it('should prevent adding images when the limit is reached', () => {
    imagemApiMock.listarPorVeiculo.mockReturnValue(
      of(
        Array.from({ length: 10 }, (_, indice) => ({
          id: indice + 1,
          nomeOriginal: `imagem-${indice + 1}.jpg`,
          objectKey: `veiculos/1/imagem-${indice + 1}.jpg`,
          principal: indice === 0,
        }))
      )
    );

    criarComponente();

    expect(component.quantidadeDisponivel()).toBe(0);
    expect(component.podeAdicionar()).toBe(false);
  });

  it('should add selected images and reload the gallery', () => {
    imagemApiMock.listarPorVeiculo.mockReturnValue(
      of(imagens)
    );
    imagemApiMock.adicionarAoVeiculo.mockReturnValue(
      of([])
    );

    criarComponente();

    const arquivo = new File(
      ['conteúdo'],
      'lateral.jpg',
      { type: 'image/jpeg' }
    );

    component.arquivosSelecionados.set([arquivo]);
    component.adicionarImagens();

    expect(imagemApiMock.adicionarAoVeiculo)
      .toHaveBeenCalledExactlyOnceWith(
        1,
        [arquivo]
      );

    expect(imagemApiMock.listarPorVeiculo)
      .toHaveBeenCalledTimes(2);

    expect(component.arquivosSelecionados())
      .toEqual([]);

    expect(component.adicionando()).toBe(false);

    expect(component.mensagemSucesso())
      .toBe('Imagem adicionada com sucesso.');
  });

  it('should define another image as the main image', () => {
    imagemApiMock.listarPorVeiculo.mockReturnValue(
      of(imagens)
    );
    imagemApiMock.definirPrincipal.mockReturnValue(
      of(undefined)
    );

    criarComponente();

    component.definirComoPrincipal(2);

    expect(imagemApiMock.definirPrincipal)
      .toHaveBeenCalledExactlyOnceWith(2);

    expect(imagemApiMock.listarPorVeiculo)
      .toHaveBeenCalledTimes(2);

    expect(component.idImagemEmProcessamento())
      .toBeNull();

    expect(component.mensagemSucesso())
      .toBe('Imagem principal alterada com sucesso.');
  });

  it('should not redefine the current main image', () => {
    imagemApiMock.listarPorVeiculo.mockReturnValue(
      of(imagens)
    );

    criarComponente();

    component.definirComoPrincipal(1);

    expect(imagemApiMock.definirPrincipal)
      .not.toHaveBeenCalled();
  });

  it('should delete an image and reload the gallery', () => {
    imagemApiMock.listarPorVeiculo.mockReturnValue(
      of(imagens)
    );
    imagemApiMock.excluir.mockReturnValue(
      of(undefined)
    );

    criarComponente();

    component.excluirImagem(2);

    expect(imagemApiMock.excluir)
      .toHaveBeenCalledExactlyOnceWith(2);

    expect(dialogMock.open)
      .toHaveBeenCalledOnce();

    expect(imagemApiMock.listarPorVeiculo)
      .toHaveBeenCalledTimes(2);

    expect(component.idImagemEmProcessamento())
      .toBeNull();

    expect(component.mensagemSucesso())
      .toBe('Imagem excluída com sucesso.');
  });

  it('should display an error when an image cannot be deleted', () => {
    imagemApiMock.listarPorVeiculo.mockReturnValue(
      of(imagens)
    );
    imagemApiMock.excluir.mockReturnValue(
      throwError(() => new Error('Falha na API'))
    );

    criarComponente();

    component.excluirImagem(2);

    expect(component.idImagemEmProcessamento())
      .toBeNull();

    expect(component.erroAcao())
      .toBe('Não foi possível excluir a imagem.');

    expect(imagemApiMock.listarPorVeiculo)
      .toHaveBeenCalledOnce();
  });
  it('should not delete an image when confirmation is cancelled', () => {
    imagemApiMock.listarPorVeiculo.mockReturnValue(
      of(imagens)
    );

    dialogMock.open.mockReturnValue({
      afterClosed: () => of(false),
    });

    criarComponente();

    component.excluirImagem(2);

    expect(dialogMock.open)
      .toHaveBeenCalledOnce();

    expect(imagemApiMock.excluir)
      .not.toHaveBeenCalled();

    expect(imagemApiMock.listarPorVeiculo)
      .toHaveBeenCalledOnce();

    expect(component.idImagemEmProcessamento())
      .toBeNull();
  });
});