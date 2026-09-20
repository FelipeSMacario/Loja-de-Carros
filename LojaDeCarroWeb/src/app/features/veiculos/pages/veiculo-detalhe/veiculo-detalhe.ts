import { ChangeDetectionStrategy, Component, computed, inject, OnInit, signal, } from '@angular/core';
import { ActivatedRoute, RouterLink, } from '@angular/router';
import { environment } from '../../../../../environments/environment';
import { VeiculoApi } from '../../data-access/veiculo-api';
import { VeiculoDetalheResponse } from '../../models/veiculo-detalhe-response';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { finalize, take } from 'rxjs';
import { AuthService } from '../../../../core/auth/auth-service';
import { DialogoConfirmacao } from '../../../../shared/components/dialogo-confirmacao/dialogo-confirmacao';
import { VendaResponse } from '../../models/venda-response';
import { VendaApi } from '../../data-access/venda-api';
import { UsuarioApi } from '../../../usuarios/data-access/usuario-api';

@Component({
  selector: 'app-veiculo-detalhe',
  imports: [MatButtonModule, MatIconModule, RouterLink,],
  templateUrl: './veiculo-detalhe.html',
  styleUrl: './veiculo-detalhe.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VeiculoDetalhe implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly veiculoApi = inject(VeiculoApi);

  private idVeiculo: number | null = null;

  readonly veiculo = signal<VeiculoDetalheResponse | null>(null);
  readonly carregando = signal(true);
  readonly erro = signal(false);
  readonly imagemSelecionadaId = signal<number | null>(null);
  readonly imagemSelecionadaFalhou = signal(false);

  readonly auth = inject(AuthService);

  private readonly dialog = inject(MatDialog);

  readonly usuarioAtualId = signal<number | null>(null);
  readonly carregandoUsuario = signal(false);
  readonly erroUsuario = signal(false);

  readonly confirmandoCompra = signal(false);
  readonly enviandoCompra = signal(false);
  readonly erroCompra = signal(false);


  private readonly usuarioApi = inject(UsuarioApi);
  private readonly vendaApi = inject(VendaApi);

  readonly vendaCriada = signal<VendaResponse | null>(null);

  readonly podeIniciarCompra = computed(() => {
    const veiculo = this.veiculo();

    if (
      this.gerenciandoMeuAnuncio
      || !this.auth.inicializado()
      || veiculo?.statusVeiculo !== 'DISPONIVEL'
      || this.vendaCriada() !== null
    ) {
      return false;
    }

    if (!this.auth.autenticado()) {
      return true; // O botão levará ao login.
    }

    return this.auth.possuiRole('USUARIO')
      && !this.carregandoUsuario()
      && !this.erroUsuario()
      && this.usuarioAtualId() !== null
      && this.usuarioAtualId() !== veiculo.vendedor.id;
  });

  readonly imagemSelecionadaUrl = computed(() => {
    const id = this.imagemSelecionadaId();


    if (id === null || this.imagemSelecionadaFalhou()) {
      return null;
    }

    return `${environment.apiUrl}/imagens/${id}/conteudo`;
  });

  readonly statusFormatado = computed(() => {
    const status = this.veiculo()?.statusVeiculo;

    if (status === undefined) {
      return '';
    }

    return {
      DISPONIVEL: 'Disponível',
      RESERVADO: 'Reservado',
      PAUSADO: 'Pausado',
      VENDIDO: 'Vendido',
    }[status];
  });

  readonly podeEditar = computed(() => {
    const status = this.veiculo()?.statusVeiculo;

    return this.gerenciandoMeuAnuncio
      && (
        status === 'DISPONIVEL'
        || status === 'PAUSADO'
      );
  });

  readonly valorFormatado = computed(() => {
    const valor = this.veiculo()?.valor;

    return valor === undefined
      ? ''
      : new Intl.NumberFormat('pt-BR', {
        style: 'currency',
        currency: 'BRL',
      }).format(valor);
  });

  readonly imagensOrdenadas = computed(() => {
    const imagens = this.veiculo()?.imagens ?? [];

    return [...imagens].sort((imagemA, imagemB) => {
      if (imagemA.principal !== imagemB.principal) {
        return imagemA.principal ? -1 : 1;
      }

      return imagemA.id - imagemB.id;
    });
  });

  readonly quilometragemFormatada = computed(() => {
    const quilometragem = this.veiculo()?.quilometragem;

    return quilometragem === undefined
      ? ''
      : new Intl.NumberFormat('pt-BR')
        .format(quilometragem);
  });

  readonly gerenciandoMeuAnuncio =
    this.route.snapshot.data?.['meuAnuncio'] === true;

  readonly rotaRetorno = this.gerenciandoMeuAnuncio
    ? '/veiculos/meus-anuncios'
    : '/home';

  readonly textoRetorno = this.gerenciandoMeuAnuncio
    ? 'Voltar aos meus anúncios'
    : 'Voltar ao estoque';

  ngOnInit(): void {
    const id = Number(
      this.route.snapshot.paramMap.get('id')
    );

    if (!Number.isInteger(id) || id <= 0) {
      this.carregando.set(false);
      this.erro.set(true);
      return;
    }

    this.idVeiculo = id;
    this.carregar();

    if (this.auth.autenticado() && !this.gerenciandoMeuAnuncio) {
      this.carregarUsuarioAtual();
    }
  }

  carregar(): void {
    if (this.idVeiculo === null) {
      return;
    }

    this.carregando.set(true);
    this.erro.set(false);

    const consulta = this.gerenciandoMeuAnuncio
      ? this.veiculoApi.buscarMeuAnuncio(this.idVeiculo)
      : this.veiculoApi.buscarPorId(this.idVeiculo);



    consulta.subscribe({
      next: veiculo => {
        this.veiculo.set(veiculo);

        const imagemPrincipal =
          veiculo.imagens.find(
            imagem => imagem.principal
          ) ?? veiculo.imagens[0];

        this.imagemSelecionadaId.set(
          imagemPrincipal?.id ?? null
        );

        this.imagemSelecionadaFalhou.set(false);
        this.carregando.set(false);
      },
      error: () => {
        this.veiculo.set(null);
        this.imagemSelecionadaId.set(null);
        this.imagemSelecionadaFalhou.set(false);
        this.carregando.set(false);
        this.erro.set(true);
      },
    });
  }
  selecionarImagem(id: number): void {
    this.imagemSelecionadaId.set(id);
    this.imagemSelecionadaFalhou.set(false);
  }

  imagemUrl(id: number): string {
    return `${environment.apiUrl}/imagens/${id}/conteudo`;
  }
  tratarFalhaImagem(): void {
    this.imagemSelecionadaFalhou.set(true);
  }

  carregarUsuarioAtual(): void {
    if (!this.auth.autenticado() || this.gerenciandoMeuAnuncio) {
      return;
    }

    this.carregandoUsuario.set(true);
    this.erroUsuario.set(false);
    this.usuarioAtualId.set(null);

    this.usuarioApi.buscarAtual()
      .pipe(finalize(() => this.carregandoUsuario.set(false)))
      .subscribe({
        next: usuario => {
          this.usuarioAtualId.set(usuario.id);
        },
        error: () => {
          this.erroUsuario.set(true);
        },
      });
  }

  iniciarCompra(): void {
    const veiculo = this.veiculo();

    if (
      !veiculo
      || !this.podeIniciarCompra()
      || this.confirmandoCompra()
      || this.enviandoCompra()
    ) {
      return;
    }

    if (!this.auth.autenticado()) {
      void this.auth.entrar();
      return;
    }

    this.confirmandoCompra.set(true);
    this.erroCompra.set(false);

    this.dialog.open(DialogoConfirmacao, {
      autoFocus: 'first-tabbable',
      disableClose: true,
      maxWidth: 'calc(100vw - 32px)',
      data: {
        titulo: 'Reservar veículo?',
        mensagem:
          `O anúncio de ${veiculo.marca} ${veiculo.modelo} `
          + 'ficará reservado para você. Você poderá cancelar '
          + 'a venda; a conclusão depende do vendedor.',
        textoConfirmar: 'Reservar veículo',
        textoCancelar: 'Voltar',
        tipo: 'aviso',
      },
    }).afterClosed().pipe(take(1))
      .subscribe(confirmado => {
        this.confirmandoCompra.set(false);

        if (!confirmado) {
          return;
        }

        this.enviandoCompra.set(true);

        this.vendaApi.criar({ veiculoId: veiculo.id })
          .pipe(finalize(() => this.enviandoCompra.set(false)))
          .subscribe({
            next: venda => {
              this.vendaCriada.set(venda);

              this.veiculo.update(atual =>
                atual?.id === venda.veiculo.id
                  ? {
                    ...atual,
                    statusVeiculo: venda.veiculo.status,
                  }
                  : atual
              );
            },
            error: () => {
              this.erroCompra.set(true);
            },
          });
      });
  }
}