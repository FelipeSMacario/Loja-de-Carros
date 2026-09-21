import { ChangeDetectionStrategy, Component, computed, effect, inject, } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { RouterLink, RouterLinkActive, } from '@angular/router';
import { UsuarioApi, } from '../../features/usuarios/data-access/usuario-api';
import { AuthService } from '../../core/auth/auth-service';

@Component({
  selector: 'app-header',
  imports: [
    MatButtonModule,
    MatIconModule,
    MatMenuModule,
    RouterLink,
    RouterLinkActive,
  ],
  templateUrl: './header.html',
  styleUrl: './header.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Header {
  readonly auth = inject(AuthService);
  private readonly usuarioApi = inject(UsuarioApi);
  private subjectCarregado: string | null = null;

  readonly nomeExibicao = computed(
    () =>
      this.usuarioApi.usuarioAtual()?.nome
      ?? this.auth.usuario()?.nome
      ?? ''
  );

  constructor() {
    effect(onCleanup => {
      const inicializado = this.auth.inicializado();
      const autenticado = this.auth.autenticado();
      const subject = this.auth.usuario()?.subject;

      if (!inicializado) {
        return;
      }

      if (!autenticado || !subject) {
        this.subjectCarregado = null;
        this.usuarioApi.limparUsuarioAtual();
        return;
      }

      if (this.subjectCarregado === subject) {
        return;
      }

      this.subjectCarregado = subject;

      const subscription =
        this.usuarioApi.buscarAtual().subscribe({
          error: () => {
            // Mantém como fallback o nome presente no token.
            this.subjectCarregado = null;
          },
        });

      onCleanup(() => {
        subscription.unsubscribe();
      });
    });
  }

  async entrar(): Promise<void> {
    await this.auth.entrar();
  }

  async sair(): Promise<void> {
    await this.auth.sair();
  }
}