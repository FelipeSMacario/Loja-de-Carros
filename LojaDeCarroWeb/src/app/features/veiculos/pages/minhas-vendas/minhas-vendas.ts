import {
  ChangeDetectionStrategy,
  Component,
  inject,
  OnInit,
  signal,
} from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import {
  MatPaginatorModule,
  PageEvent,
} from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import {
  finalize,
  Observable,
} from 'rxjs';

import {
  DialogoConfirmacao,
} from '../../../../shared/components/dialogo-confirmacao/dialogo-confirmacao';
import { VendaCard } from '../../components/venda-card/venda-card';
import { VendaApi } from '../../data-access/venda-api';
import {
  StatusVenda,
  VendaResponse,
} from '../../models/venda-response';

interface OpcaoStatusVenda {
  valor: StatusVenda | null;
  rotulo: string;
}

@Component({
  selector: 'app-minhas-vendas',
  imports: [
    MatButtonModule,
    MatIconModule,
    MatPaginatorModule,
    MatProgressSpinnerModule,
    VendaCard,
  ],
  templateUrl: './minhas-vendas.html',
  styleUrl: './minhas-vendas.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MinhasVendas implements OnInit {
  private readonly vendaApi = inject(VendaApi);
  private readonly dialog = inject(MatDialog);

  readonly vendas = signal<VendaResponse[]>([]);
  readonly carregando = signal(true);
  readonly erro = signal(false);
  readonly erroAcao = signal(false);
  readonly acaoEmAndamentoId = signal<number | null>(null);

  readonly pagina = signal(0);
  readonly tamanhoPagina = signal(9);
  readonly totalElementos = signal(0);
  readonly statusSelecionado =
    signal<StatusVenda | null>(null);

  readonly opcoesStatus: readonly OpcaoStatusVenda[] = [
    {
      valor: null,
      rotulo: 'Todas',
    },
    {
      valor: 'EM_ANDAMENTO',
      rotulo: 'Em andamento',
    },
    {
      valor: 'CONCLUIDA',
      rotulo: 'Concluídas',
    },
    {
      valor: 'CANCELADA',
      rotulo: 'Canceladas',
    },
  ];

  ngOnInit(): void {
    this.carregar();
  }

  carregar(): void {
    this.carregando.set(true);
    this.erro.set(false);

    this.vendaApi.listarMinhasVendas(
      this.pagina(),
      this.tamanhoPagina(),
      this.statusSelecionado() ?? undefined
    ).subscribe({
      next: resposta => {
        this.vendas.set(resposta.content);
        this.totalElementos.set(resposta.totalElements);
        this.carregando.set(false);
      },
      error: () => {
        this.vendas.set([]);
        this.totalElementos.set(0);
        this.carregando.set(false);
        this.erro.set(true);
      },
    });
  }

  alterarStatus(status: StatusVenda | null): void {
    this.statusSelecionado.set(status);
    this.pagina.set(0);
    this.carregar();
  }

  alterarPagina(evento: PageEvent): void {
    this.pagina.set(evento.pageIndex);
    this.tamanhoPagina.set(evento.pageSize);
    this.carregar();
  }

  cancelar(idVenda: number): void {
    if (this.acaoEmAndamentoId() !== null) {
      return;
    }

    const venda = this.buscarVenda(idVenda);

    const referencia = this.dialog.open(
      DialogoConfirmacao,
      {
        autoFocus: 'first-tabbable',
        disableClose: true,
        maxWidth: 'calc(100vw - 32px)',
        data: {
          titulo: 'Cancelar venda?',
          mensagem:
            `A negociação do ${venda?.veiculo.marca ?? 'veículo'} `
            + `${venda?.veiculo.modelo ?? ''} será cancelada `
            + 'e o anúncio voltará a ficar disponível.',
          textoConfirmar: 'Cancelar venda',
          textoCancelar: 'Manter negociação',
          tipo: 'perigo',
        },
      }
    );

    referencia.afterClosed().subscribe(confirmado => {
      if (confirmado) {
        this.executarAcao(
          idVenda,
          this.vendaApi.cancelar(idVenda)
        );
      }
    });
  }

  concluir(idVenda: number): void {
    if (this.acaoEmAndamentoId() !== null) {
      return;
    }

    const venda = this.buscarVenda(idVenda);

    const referencia = this.dialog.open(
      DialogoConfirmacao,
      {
        autoFocus: 'first-tabbable',
        disableClose: true,
        maxWidth: 'calc(100vw - 32px)',
        data: {
          titulo: 'Concluir venda?',
          mensagem:
            `Confirme que a venda do `
            + `${venda?.veiculo.marca ?? 'veículo'} `
            + `${venda?.veiculo.modelo ?? ''} foi realizada. `
            + 'O anúncio será marcado como vendido.',
          textoConfirmar: 'Concluir venda',
          textoCancelar: 'Voltar',
          tipo: 'aviso',
        },
      }
    );

    referencia.afterClosed().subscribe(confirmado => {
      if (confirmado) {
        this.executarAcao(
          idVenda,
          this.vendaApi.concluir(idVenda)
        );
      }
    });
  }

  private buscarVenda(
    idVenda: number
  ): VendaResponse | undefined {
    return this.vendas().find(
      venda => venda.id === idVenda
    );
  }

  private executarAcao(
    idVenda: number,
    acao: Observable<VendaResponse>
  ): void {
    if (this.acaoEmAndamentoId() !== null) {
      return;
    }

    this.acaoEmAndamentoId.set(idVenda);
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