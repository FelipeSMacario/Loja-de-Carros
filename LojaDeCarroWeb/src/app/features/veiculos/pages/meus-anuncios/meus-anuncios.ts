import { ChangeDetectionStrategy, Component, inject, OnInit, signal, } from '@angular/core';
import { PageEvent } from '@angular/material/paginator';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { RouterLink } from '@angular/router';
import { Observable, finalize } from 'rxjs';
import { VeiculoCard } from '../../components/veiculo-card/veiculo-card';
import { VeiculoApi } from '../../data-access/veiculo-api';
import { StatusVeiculo, VeiculoResponse, } from '../../models/veiculo-response';
import { MatDialog } from '@angular/material/dialog';

import { DialogoConfirmacao } from '../../../../shared/components/dialogo-confirmacao/dialogo-confirmacao';

interface OpcaoStatus {
  valor: StatusVeiculo | null;
  rotulo: string;
}

@Component({
  selector: 'app-meus-anuncios',
  imports: [
    MatButtonModule,
    MatIconModule,
    MatPaginatorModule,
    MatProgressSpinnerModule,
    RouterLink,
    VeiculoCard,
  ],
  templateUrl: './meus-anuncios.html',
  styleUrl: './meus-anuncios.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MeusAnuncios implements OnInit {
  private readonly veiculoApi = inject(VeiculoApi);
  private readonly dialog = inject(MatDialog);


  readonly veiculos = signal<VeiculoResponse[]>([]);
  readonly carregando = signal(true);
  readonly erro = signal(false);
  readonly acaoEmAndamentoId = signal<number | null>(null);
  readonly erroAcao = signal(false);

  readonly rotulosStatus: Record<StatusVeiculo, string> = {
    DISPONIVEL: 'Disponível',
    RESERVADO: 'Reservado',
    PAUSADO: 'Pausado',
    VENDIDO: 'Vendido',
  };
  readonly pagina = signal(0);
  readonly tamanhoPagina = signal(9);
  readonly totalElementos = signal(0);
  readonly statusSelecionado =
    signal<StatusVeiculo | null>(null);

  readonly opcoesStatus: readonly OpcaoStatus[] = [
    { valor: null, rotulo: 'Todos' },
    { valor: 'DISPONIVEL', rotulo: 'Disponíveis' },
    { valor: 'RESERVADO', rotulo: 'Reservados' },
    { valor: 'PAUSADO', rotulo: 'Pausados' },
    { valor: 'VENDIDO', rotulo: 'Vendidos' },
  ];

  ngOnInit(): void {
    this.carregar();
  }

  carregar(): void {
    this.carregando.set(true);
    this.erro.set(false);

    this.veiculoApi.listarMeusAnuncios(
      this.pagina(),
      this.tamanhoPagina(),
      this.statusSelecionado() ?? undefined
    ).subscribe({
      next: resposta => {
        this.veiculos.set(resposta.content);
        this.totalElementos.set(resposta.totalElements);
        this.carregando.set(false);
      },
      error: () => {
        this.veiculos.set([]);
        this.totalElementos.set(0);
        this.carregando.set(false);
        this.erro.set(true);
      },
    });
  }

  alterarStatus(status: StatusVeiculo | null): void {
    this.statusSelecionado.set(status);
    this.pagina.set(0);
    this.carregar();
  }

  alterarPagina(evento: PageEvent): void {
    this.pagina.set(evento.pageIndex);
    this.tamanhoPagina.set(evento.pageSize);
    this.carregar();
  }
  pausar(id: number): void {
    if (this.acaoEmAndamentoId() !== null) {
      return;
    }

    const veiculo = this.veiculos().find(
      item => item.id === id
    );

    const referencia = this.dialog.open(
      DialogoConfirmacao,
      {
        autoFocus: 'first-tabbable',
        disableClose: true,
        maxWidth: 'calc(100vw - 32px)',
        data: {
          titulo: 'Pausar anúncio?',
          mensagem:
            `O anúncio do ${veiculo?.marca ?? 'veículo'} `
            + `${veiculo?.modelo ?? ''} deixará de aparecer `
            + 'no estoque público. Você poderá reativá-lo depois.',
          textoConfirmar: 'Pausar anúncio',
          textoCancelar: 'Cancelar',
          tipo: 'aviso',
        },
      }
    );

    referencia.afterClosed().subscribe(confirmado => {
      if (confirmado) {
        this.executarAcao(
          id,
          this.veiculoApi.pausar(id)
        );
      }
    });
  }

  reativar(id: number): void {
    this.executarAcao(
      id,
      this.veiculoApi.reativar(id)
    );
  }

  private executarAcao(
    id: number,
    acao: Observable<VeiculoResponse>
  ): void {
    if (this.acaoEmAndamentoId() !== null) {
      return;
    }

    this.acaoEmAndamentoId.set(id);
    this.erroAcao.set(false);

    acao.pipe(
      finalize(() => {
        this.acaoEmAndamentoId.set(null);
      })
    ).subscribe({
      next: () => {
        this.carregar();
      },
      error: () => {
        this.erroAcao.set(true);
      },
    });
  }
}