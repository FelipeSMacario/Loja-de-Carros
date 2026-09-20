import {
  ChangeDetectionStrategy,
  Component,
  computed,
  inject,
  OnInit,
  signal,
} from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { finalize } from 'rxjs';

import { AuthService } from '../../../../core/auth/auth-service';
import {
  DialogoConfirmacao,
} from '../../../../shared/components/dialogo-confirmacao/dialogo-confirmacao';
import {
  UsuarioApi,
  UsuarioAtualResponse,
} from '../../data-access/usuario-api';

interface ApiErrorResponse {
  message?: string;
  mensagem?: string;
  detail?: string;
}

@Component({
  selector: 'app-minha-conta',
  imports: [
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './minha-conta.html',
  styleUrl: './minha-conta.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MinhaConta implements OnInit {
  private readonly usuarioApi = inject(UsuarioApi);
  private readonly auth = inject(AuthService);
  private readonly dialog = inject(MatDialog);

  readonly usuario =
    signal<UsuarioAtualResponse | null>(null);

  readonly carregando = signal(true);
  readonly erroCarregamento = signal(false);
  readonly desativando = signal(false);

  readonly erroDesativacao =
    signal<string | null>(null);

  readonly cpfFormatado = computed(() => {
    const cpf = this.usuario()
      ?.cpf
      .replace(/\D/g, '');

    if (!cpf || cpf.length !== 11) {
      return '***.***.***-**';
    }

    return `***.***.${cpf.slice(6, 9)}-${cpf.slice(9)}`;
  });

  ngOnInit(): void {
    this.carregar();
  }

  carregar(): void {
    this.carregando.set(true);
    this.erroCarregamento.set(false);

    this.usuarioApi.buscarAtual().subscribe({
      next: usuario => {
        this.usuario.set(usuario);
        this.carregando.set(false);
      },
      error: () => {
        this.usuario.set(null);
        this.carregando.set(false);
        this.erroCarregamento.set(true);
      },
    });
  }

  solicitarDesativacao(): void {
    if (this.desativando()) {
      return;
    }

    const referencia = this.dialog.open(
      DialogoConfirmacao,
      {
        autoFocus: 'first-tabbable',
        disableClose: true,
        maxWidth: 'calc(100vw - 32px)',
        data: {
          titulo: 'Desativar sua conta?',
          mensagem:
            'Seus anúncios disponíveis serão pausados. '
            + 'A operação não será permitida se você possuir '
            + 'compras, vendas ou veículos reservados. '
            + 'Depois da desativação, o cadastro somente poderá '
            + 'ser reativado por um administrador.',
          textoConfirmar: 'Desativar conta',
          textoCancelar: 'Manter minha conta',
          tipo: 'perigo',
        },
      }
    );

    referencia.afterClosed().subscribe(confirmado => {
      if (confirmado) {
        this.desativar();
      }
    });
  }

  private desativar(): void {
    if (this.desativando()) {
      return;
    }

    this.desativando.set(true);
    this.erroDesativacao.set(null);

    this.usuarioApi.desativar().pipe(
      finalize(() => {
        this.desativando.set(false);
      })
    ).subscribe({
      next: async () => {
        await this.auth.sair();
      },
      error: erro => {
        this.erroDesativacao.set(
          this.obterMensagemErro(erro)
        );
      },
    });
  }

  private obterMensagemErro(erro: unknown): string {
    const mensagemPadrao =
      'Não foi possível desativar sua conta. '
      + 'Verifique se existem negociações em andamento.';

    if (!(erro instanceof HttpErrorResponse)) {
      return mensagemPadrao;
    }

    if (typeof erro.error === 'string') {
      return erro.error || mensagemPadrao;
    }

    const resposta =
      erro.error as ApiErrorResponse | null;

    return resposta?.detail
      ?? resposta?.message
      ?? resposta?.mensagem
      ?? mensagemPadrao;
  }
}