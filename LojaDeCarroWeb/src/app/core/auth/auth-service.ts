import {
  computed,
  inject,
  Injectable,
  signal,
} from '@angular/core';

import { KEYCLOAK_INSTANCE } from './keycloak-instance';

export interface UsuarioAutenticado {
  subject: string;
  nome: string;
  email: string | null;
  roles: readonly string[];
}

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly keycloak = inject(KEYCLOAK_INSTANCE);

  readonly inicializado = signal(false);
  readonly autenticado = signal(false);
  readonly falhaInicializacao = signal(false);
  readonly usuario = signal<UsuarioAutenticado | null>(null);

  readonly roles = computed(
    () => this.usuario()?.roles ?? []
  );

  constructor() {
    this.configurarEventos();
  }

  async inicializar(): Promise<void> {
    try {
      await this.keycloak.init({
        onLoad: 'check-sso',
        pkceMethod: 'S256',
        checkLoginIframe: false,
        silentCheckSsoRedirectUri:
          `${window.location.origin}/silent-check-sso.html`,
      });

      this.falhaInicializacao.set(false);
      this.sincronizarSessao();
    } catch {
      this.limparSessao();
      this.falhaInicializacao.set(true);
    } finally {
      this.inicializado.set(true);
    }
  }

  entrar(
    redirectUri = window.location.href
  ): Promise<void> {
    return this.keycloak.login({
      redirectUri,
    });
  }

  sair(): Promise<void> {
    return this.keycloak.logout({
      redirectUri: `${window.location.origin}/home`,
    });
  }

  possuiRole(role: string): boolean {
    return this.roles().includes(role);
  }

  async obterTokenValido(
    validadeMinimaSegundos = 30
  ): Promise<string | null> {
    if (!this.keycloak.authenticated) {
      return null;
    }

    try {
      await this.keycloak.updateToken(
        validadeMinimaSegundos
      );

      this.sincronizarSessao();

      return this.keycloak.token ?? null;
    } catch {
      this.limparSessao();
      return null;
    }
  }

  private configurarEventos(): void {
    this.keycloak.onAuthSuccess = () => {
      this.sincronizarSessao();
    };

    this.keycloak.onAuthRefreshSuccess = () => {
      this.sincronizarSessao();
    };

    this.keycloak.onAuthLogout = () => {
      this.limparSessao();
    };

    this.keycloak.onAuthRefreshError = () => {
      this.limparSessao();
    };

    this.keycloak.onTokenExpired = () => {
      void this.obterTokenValido();
    };
  }

  private sincronizarSessao(): void {
    const token = this.keycloak.tokenParsed;

    if (!this.keycloak.authenticated || !token?.sub) {
      this.limparSessao();
      return;
    }

    const nome =
      this.obterClaimTexto(token, 'name')
      ?? this.obterClaimTexto(
        token,
        'preferred_username'
      )
      ?? 'Usuário';

    this.autenticado.set(true);

    this.usuario.set({
      subject: token.sub,
      nome,
      email: this.obterClaimTexto(token, 'email'),
      roles: [
        ...(this.keycloak.realmAccess?.roles ?? []),
      ],
    });
  }

  private obterClaimTexto(
    token: Record<string, unknown>,
    claim: string
  ): string | null {
    const valor = token[claim];

    return typeof valor === 'string'
      ? valor
      : null;
  }

  private limparSessao(): void {
    this.autenticado.set(false);
    this.usuario.set(null);
  }
}