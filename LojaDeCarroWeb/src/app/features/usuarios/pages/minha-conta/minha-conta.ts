import { HttpErrorResponse } from '@angular/common/http';
import {
  ChangeDetectionStrategy,
  Component,
  computed,
  inject,
  OnInit,
  signal,
} from '@angular/core';
import {
  FormBuilder,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import {
  MatProgressSpinnerModule,
} from '@angular/material/progress-spinner';
import { finalize } from 'rxjs';

import {
  AuthService,
} from '../../../../core/auth/auth-service';
import {
  DialogoConfirmacao,
} from '../../../../shared/components/dialogo-confirmacao/dialogo-confirmacao';
import {
  UsuarioApi,
  UsuarioAtualResponse,
  UsuarioUpdateRequest,
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
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatProgressSpinnerModule,
    ReactiveFormsModule,
  ],
  templateUrl: './minha-conta.html',
  styleUrl: './minha-conta.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MinhaConta implements OnInit {
  private readonly usuarioApi = inject(UsuarioApi);
  private readonly auth = inject(AuthService);
  private readonly dialog = inject(MatDialog);
  private readonly formBuilder = inject(FormBuilder);

  readonly usuario =
    signal<UsuarioAtualResponse | null>(null);

  readonly carregando = signal(true);
  readonly erroCarregamento = signal(false);

  readonly editando = signal(false);
  readonly salvando = signal(false);
  readonly atualizacaoConcluida = signal(false);

  readonly erroAtualizacao =
    signal<string | null>(null);

  readonly desativando = signal(false);

  readonly erroDesativacao =
    signal<string | null>(null);

  readonly formulario = this.formBuilder.nonNullable.group({
    nome: [
      '',
      [
        Validators.required,
        Validators.pattern(/\S/),
      ],
    ],
    dataNascimento: [
      '',
      [
        Validators.required,
      ],
    ],
  });

  readonly cpfFormatado = computed(() => {
    const cpf = this.usuario()
      ?.cpf
      .replace(/\D/g, '');

    if (!cpf || cpf.length !== 11) {
      return '***.***.***-**';
    }

    return `***.***.${cpf.slice(6, 9)}-${cpf.slice(9)}`;
  });

  readonly dataNascimentoFormatada = computed(() => {
    const data = this.usuario()?.dataNascimento;

    if (!data) {
      return '';
    }

    const [ano, mes, dia] = data.split('-');

    if (!ano || !mes || !dia) {
      return data;
    }

    return `${dia}/${mes}/${ano}`;
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

  iniciarEdicao(): void {
    const usuario = this.usuario();

    if (!usuario || this.salvando()) {
      return;
    }

    this.formulario.reset({
      nome: usuario.nome,
      dataNascimento: usuario.dataNascimento,
    });

    this.erroAtualizacao.set(null);
    this.atualizacaoConcluida.set(false);
    this.editando.set(true);
  }

  cancelarEdicao(): void {
    if (this.salvando()) {
      return;
    }

    this.editando.set(false);
    this.erroAtualizacao.set(null);
    this.atualizacaoConcluida.set(false);
  }

  salvarAlteracoes(): void {
    if (this.salvando()) {
      return;
    }

    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }

    const request: UsuarioUpdateRequest =
      this.formulario.getRawValue();

    this.salvando.set(true);
    this.erroAtualizacao.set(null);
    this.atualizacaoConcluida.set(false);

    this.usuarioApi.atualizar(request).pipe(
      finalize(() => {
        this.salvando.set(false);
      })
    ).subscribe({
      next: usuario => {
        this.usuario.set(usuario);
        this.editando.set(false);
        this.atualizacaoConcluida.set(true);
      },
      error: erro => {
        this.erroAtualizacao.set(
          this.obterMensagemErro(
            erro,
            'Não foi possível atualizar seus dados. '
            + 'Revise as informações e tente novamente.'
          )
        );
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
          this.obterMensagemErro(
            erro,
            'Não foi possível desativar sua conta. '
            + 'Verifique se existem negociações em andamento.'
          )
        );
      },
    });
  }

  private obterMensagemErro(
    erro: unknown,
    mensagemPadrao: string
  ): string {
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