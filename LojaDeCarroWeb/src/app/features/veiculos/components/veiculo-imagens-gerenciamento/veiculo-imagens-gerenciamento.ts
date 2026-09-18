import { ChangeDetectionStrategy, Component, computed, inject, input, OnInit, signal, } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { environment } from '../../../../../environments/environment';
import { ImagemApi } from '../../data-access/imagem-api';
import { ImagemResponse } from '../../models/imagem-response';
import { MatDialog } from '@angular/material/dialog';
import { DialogoConfirmacao } from '../../../../shared/components/dialogo-confirmacao/dialogo-confirmacao';

@Component({
  selector: 'app-veiculo-imagens-gerenciamento',
  imports: [MatButtonModule, MatIconModule, MatProgressSpinnerModule,],
  templateUrl: './veiculo-imagens-gerenciamento.html',
  styleUrl: './veiculo-imagens-gerenciamento.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VeiculoImagensGerenciamento implements OnInit {
  private readonly imagemApi = inject(ImagemApi);
  private readonly dialog = inject(MatDialog);


  readonly idVeiculo = input.required<number>();

  readonly imagens = signal<ImagemResponse[]>([]);
  readonly carregando = signal(true);
  readonly erro = signal(false);

  readonly limiteImagens = 10;

  readonly quantidadeDisponivel = computed(() =>
    Math.max(
      this.limiteImagens - this.imagens().length,
      0
    )
  );

  readonly podeAdicionar = computed(() =>
    this.quantidadeDisponivel() > 0
  );
  readonly arquivosSelecionados = signal<readonly File[]>([]);
  readonly adicionando = signal(false);
  readonly idImagemEmProcessamento = signal<number | null>(null);
  readonly erroAcao = signal<string | null>(null);
  readonly mensagemSucesso = signal<string | null>(null);

  readonly processando = computed(() =>
    this.adicionando()
    || this.idImagemEmProcessamento() !== null
  );
  ngOnInit(): void {
    this.carregar();
  }

  carregar(): void {
    this.carregando.set(true);
    this.erro.set(false);

    this.imagemApi.listarPorVeiculo(
      this.idVeiculo()
    ).subscribe({
      next: imagens => {
        this.imagens.set(this.ordenarImagens(imagens));
        this.carregando.set(false);
      },
      error: () => {
        this.imagens.set([]);
        this.carregando.set(false);
        this.erro.set(true);
      },
    });
  }

  imagemUrl(idImagem: number): string {
    return `${environment.apiUrl}/imagens/${idImagem}/conteudo`;
  }

  selecionarArquivos(event: Event): void {
    const inputArquivo = event.target as HTMLInputElement;
    const arquivos = Array.from(inputArquivo.files ?? []);

    // Permite selecionar novamente o mesmo arquivo.
    inputArquivo.value = '';

    this.erroAcao.set(null);
    this.mensagemSucesso.set(null);

    if (arquivos.length > this.quantidadeDisponivel()) {
      this.arquivosSelecionados.set([]);
      this.erroAcao.set(
        `Você pode adicionar no máximo `
        + `${this.quantidadeDisponivel()} imagem(ns).`
      );
      return;
    }

    this.arquivosSelecionados.set(arquivos);
  }

  adicionarImagens(): void {
    const arquivos = this.arquivosSelecionados();

    if (
      arquivos.length === 0
      || this.adicionando()
      || arquivos.length > this.quantidadeDisponivel()
    ) {
      return;
    }

    this.adicionando.set(true);
    this.erroAcao.set(null);
    this.mensagemSucesso.set(null);

    this.imagemApi.adicionarAoVeiculo(
      this.idVeiculo(),
      arquivos
    ).subscribe({
      next: () => {
        this.arquivosSelecionados.set([]);
        this.adicionando.set(false);
        this.mensagemSucesso.set(
          arquivos.length === 1
            ? 'Imagem adicionada com sucesso.'
            : 'Imagens adicionadas com sucesso.'
        );
        this.carregar();
      },
      error: () => {
        this.adicionando.set(false);
        this.erroAcao.set(
          'Não foi possível adicionar as imagens.'
        );
      },
    });
  }

  definirComoPrincipal(idImagem: number): void {
    const imagem = this.imagens().find(
      item => item.id === idImagem
    );

    if (
      imagem?.principal
      || this.idImagemEmProcessamento() !== null
    ) {
      return;
    }

    this.iniciarProcessamento(idImagem);

    this.imagemApi.definirPrincipal(idImagem)
      .subscribe({
        next: () => {
          this.finalizarProcessamento(
            'Imagem principal alterada com sucesso.'
          );
        },
        error: () => {
          this.falharProcessamento(
            'Não foi possível alterar a imagem principal.'
          );
        },
      });
  }

  excluirImagem(idImagem: number): void {
    if (this.processando()) {
      return;
    }

    const imagem = this.imagens().find(
      item => item.id === idImagem
    );

    const referencia = this.dialog.open(
      DialogoConfirmacao,
      {
        autoFocus: 'first-tabbable',
        disableClose: true,
        maxWidth: 'calc(100vw - 32px)',
        data: {
          titulo: 'Excluir imagem?',
          mensagem:
            `A imagem "${imagem?.nomeOriginal ?? 'selecionada'}" `
            + 'será removida permanentemente.',
          textoConfirmar: 'Excluir',
          textoCancelar: 'Cancelar',
          tipo: 'perigo',
        },
      }
    );

    referencia.afterClosed().subscribe(confirmado => {
      if (confirmado) {
        this.executarExclusao(idImagem);
      }
    });
  }

  private executarExclusao(idImagem: number): void {
    this.iniciarProcessamento(idImagem);

    this.imagemApi.excluir(idImagem)
      .subscribe({
        next: () => {
          this.finalizarProcessamento(
            'Imagem excluída com sucesso.'
          );
        },
        error: () => {
          this.falharProcessamento(
            'Não foi possível excluir a imagem.'
          );
        },
      });
  }

  private iniciarProcessamento(idImagem: number): void {
    this.idImagemEmProcessamento.set(idImagem);
    this.erroAcao.set(null);
    this.mensagemSucesso.set(null);
  }

  private finalizarProcessamento(
    mensagem: string
  ): void {
    this.idImagemEmProcessamento.set(null);
    this.mensagemSucesso.set(mensagem);
    this.carregar();
  }

  private falharProcessamento(mensagem: string): void {
    this.idImagemEmProcessamento.set(null);
    this.erroAcao.set(mensagem);
  }

  private ordenarImagens(
    imagens: readonly ImagemResponse[]
  ): ImagemResponse[] {
    return [...imagens].sort((imagemA, imagemB) => {
      if (imagemA.principal !== imagemB.principal) {
        return imagemA.principal ? -1 : 1;
      }

      return imagemA.id - imagemB.id;
    });
  }
}