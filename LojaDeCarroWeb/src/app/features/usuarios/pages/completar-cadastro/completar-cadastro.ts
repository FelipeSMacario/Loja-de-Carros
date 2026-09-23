import { HttpErrorResponse } from '@angular/common/http';
import {
  ChangeDetectionStrategy,
  Component,
  computed,
  inject,
  signal,
} from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  ReactiveFormsModule,
  ValidationErrors,
  ValidatorFn,
  Validators,
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import {
  MatProgressSpinnerModule,
} from '@angular/material/progress-spinner';
import {
  ActivatedRoute,
  Router,
} from '@angular/router';
import { finalize } from 'rxjs';

import {
  AuthService,
} from '../../../../core/auth/auth-service';
import {
  UsuarioApi,
  UsuarioCadastroRequest,
} from '../../data-access/usuario-api';

interface ApiErrorResponse {
  detail?: string;
  message?: string;
  mensagem?: string;
}

function cpfValidator(): ValidatorFn {
  return (
    control: AbstractControl
  ): ValidationErrors | null => {
    const cpf = String(control.value ?? '')
      .replace(/\D/g, '');

    if (!cpf) {
      return null;
    }

    if (
      cpf.length !== 11
      || /^(\d)\1{10}$/.test(cpf)
    ) {
      return { cpf: true };
    }

    const calcularDigito = (
      quantidade: number
    ): number => {
      let soma = 0;

      for (
        let indice = 0;
        indice < quantidade;
        indice++
      ) {
        soma += Number(cpf[indice])
          * (quantidade + 1 - indice);
      }

      const resto = (soma * 10) % 11;

      return resto === 10 ? 0 : resto;
    };

    const primeiroDigito = calcularDigito(9);
    const segundoDigito = calcularDigito(10);

    return primeiroDigito === Number(cpf[9])
      && segundoDigito === Number(cpf[10])
      ? null
      : { cpf: true };
  };
}

@Component({
  selector: 'app-completar-cadastro',
  imports: [
    MatButtonModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatProgressSpinnerModule,
    ReactiveFormsModule,
  ],
  templateUrl: './completar-cadastro.html',
  styleUrl: './completar-cadastro.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CompletarCadastro {
  readonly emailAutenticado = computed(
    () => this.auth.usuario()?.email ?? ''
  );
  private readonly formBuilder = inject(FormBuilder);
  private readonly usuarioApi = inject(UsuarioApi);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  readonly enviando = signal(false);
  readonly erroCadastro = signal<string | null>(null);

  readonly dataMaxima = this.obterDataMaxima();

  readonly formulario = this.formBuilder.nonNullable.group({
    nome: [
      this.auth.usuario()?.nome ?? '',
      [
        Validators.required,
        Validators.pattern(/\S/),
      ],
    ],
    cpf: [
      '',
      [
        Validators.required,
        cpfValidator(),
      ],
    ],
    dataNascimento: [
      '',
      [
        Validators.required,
      ],
    ],
  });

  formatarCpf(evento: Event): void {
    const input = evento.target as HTMLInputElement;

    const cpf = input.value
      .replace(/\D/g, '')
      .slice(0, 11);

    const cpfFormatado = cpf
      .replace(/^(\d{3})(\d)/, '$1.$2')
      .replace(/^(\d{3})\.(\d{3})(\d)/, '$1.$2.$3')
      .replace(
        /^(\d{3})\.(\d{3})\.(\d{3})(\d)/,
        '$1.$2.$3-$4'
      );

    this.formulario.controls.cpf.setValue(
      cpfFormatado
    );
  }

  concluirCadastro(): void {
    if (this.enviando()) {
      return;
    }

    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }

    const valores = this.formulario.getRawValue();

    const request: UsuarioCadastroRequest = {
      nome: valores.nome.trim(),
      cpf: valores.cpf.replace(/\D/g, ''),
      dataNascimento: valores.dataNascimento,
    };

    this.enviando.set(true);
    this.erroCadastro.set(null);

    this.usuarioApi.criar(request).pipe(
      finalize(() => {
        this.enviando.set(false);
      })
    ).subscribe({
      next: () => {
        void this.router.navigateByUrl(
          this.obterDestino()
        );
      },
      error: erro => {
        this.erroCadastro.set(
          this.obterMensagemErro(erro)
        );
      },
    });
  }

  private obterDestino(): string {
    const returnUrl =
      this.route.snapshot.queryParamMap.get(
        'returnUrl'
      );

    if (
      returnUrl
      && returnUrl.startsWith('/')
      && !returnUrl.startsWith('//')
      && !returnUrl.startsWith('/completar-cadastro')
    ) {
      return returnUrl;
    }

    return '/home';
  }

  private obterDataMaxima(): string {
    const data = new Date();
    data.setDate(data.getDate() - 1);

    const ano = data.getFullYear();
    const mes = String(
      data.getMonth() + 1
    ).padStart(2, '0');
    const dia = String(
      data.getDate()
    ).padStart(2, '0');

    return `${ano}-${mes}-${dia}`;
  }

  private obterMensagemErro(erro: unknown): string {
    const mensagemPadrao =
      'Não foi possível concluir seu cadastro. '
      + 'Revise os dados e tente novamente.';

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