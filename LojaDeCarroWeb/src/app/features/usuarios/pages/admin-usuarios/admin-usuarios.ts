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
  MatProgressSpinnerModule,
} from '@angular/material/progress-spinner';
import { finalize } from 'rxjs';
import { DatePipe } from '@angular/common';
import {
  DialogoConfirmacao,
} from '../../../../shared/components/dialogo-confirmacao/dialogo-confirmacao';
import {
  AdminUsuarioApi,
  StatusFiltroUsuario,
} from '../../data-access/admin-usuario-api';
import {
  UsuarioAtualResponse,
} from '../../data-access/usuario-api';

interface OpcaoFiltroUsuario {
  valor: StatusFiltroUsuario;
  rotulo: string;
}

@Component({
  selector: 'app-admin-usuarios',
  imports: [
    DatePipe,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './admin-usuarios.html',
  styleUrl: './admin-usuarios.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AdminUsuarios implements OnInit {
  private readonly adminUsuarioApi =
    inject(AdminUsuarioApi);

  formatarCpf(cpf: string): string {
    const numeros = cpf.replace(/\D/g, '');

    if (numeros.length !== 11) {
      return '***.***.***-**';
    }

    return `***.***.${numeros.slice(6, 9)}-${numeros.slice(9)}`;
  }

  private readonly dialog = inject(MatDialog);

  readonly usuarios =
    signal<UsuarioAtualResponse[]>([]);

  readonly carregando = signal(true);
  readonly erroCarregamento = signal(false);

  readonly filtroSelecionado =
    signal<StatusFiltroUsuario>('TODAS');

  readonly acaoEmAndamentoId =
    signal<number | null>(null);

  readonly erroAcao = signal(false);

  readonly opcoesFiltro:
    readonly OpcaoFiltroUsuario[] = [
      {
        valor: 'TODAS',
        rotulo: 'Todos',
      },
      {
        valor: 'ATIVAS',
        rotulo: 'Ativos',
      },
      {
        valor: 'INATIVAS',
        rotulo: 'Inativos',
      },
    ];

  ngOnInit(): void {
    this.carregar();
  }

  carregar(): void {
    this.carregando.set(true);
    this.erroCarregamento.set(false);

    this.adminUsuarioApi.listar(
      this.filtroSelecionado()
    ).subscribe({
      next: usuarios => {
        this.usuarios.set(usuarios);
        this.carregando.set(false);
      },
      error: () => {
        this.usuarios.set([]);
        this.carregando.set(false);
        this.erroCarregamento.set(true);
      },
    });
  }

  alterarFiltro(
    filtro: StatusFiltroUsuario
  ): void {
    if (
      filtro === this.filtroSelecionado()
      || this.acaoEmAndamentoId() !== null
    ) {
      return;
    }

    this.filtroSelecionado.set(filtro);
    this.carregar();
  }

  solicitarAlteracaoStatus(
    usuario: UsuarioAtualResponse
  ): void {
    if (this.acaoEmAndamentoId() !== null) {
      return;
    }

    const ativar = !usuario.ativo;

    const referencia = this.dialog.open(
      DialogoConfirmacao,
      {
        autoFocus: 'first-tabbable',
        disableClose: true,
        maxWidth: 'calc(100vw - 32px)',
        data: {
          titulo: ativar
            ? 'Reativar usuário?'
            : 'Desativar usuário?',
          mensagem: ativar
            ? `O acesso de ${usuario.nome} será reativado.`
            : `O acesso de ${usuario.nome} será desativado. `
            + 'Anúncios disponíveis serão pausados e a '
            + 'operação poderá ser bloqueada se existirem '
            + 'negociações em andamento.',
          textoConfirmar: ativar
            ? 'Reativar usuário'
            : 'Desativar usuário',
          textoCancelar: 'Cancelar',
          tipo: ativar ? 'aviso' : 'perigo',
        },
      }
    );

    referencia.afterClosed().subscribe(confirmado => {
      if (confirmado) {
        this.alterarStatus(
          usuario.id,
          ativar
        );
      }
    });
  }

  private alterarStatus(
    id: number,
    ativo: boolean
  ): void {
    this.acaoEmAndamentoId.set(id);
    this.erroAcao.set(false);

    this.adminUsuarioApi
      .alterarStatus(id, ativo)
      .pipe(
        finalize(() => {
          this.acaoEmAndamentoId.set(null);
        })
      )
      .subscribe({
        next: () => {
          this.carregar();
        },
        error: () => {
          this.erroAcao.set(true);
        },
      });
  }
}