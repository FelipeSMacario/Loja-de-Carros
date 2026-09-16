import { TestBed } from '@angular/core/testing';
import Keycloak from 'keycloak-js';
import { vi } from 'vitest';

import { AuthService } from './auth-service';
import { KEYCLOAK_INSTANCE } from './keycloak-instance';

describe('AuthService', () => {
  let service: AuthService;
  let keycloakMock: Keycloak;

  beforeEach(() => {
    keycloakMock = {
      init: vi.fn().mockResolvedValue(false),
      login: vi.fn().mockResolvedValue(undefined),
      logout: vi.fn().mockResolvedValue(undefined),
      updateToken: vi.fn().mockResolvedValue(false),
      authenticated: false,
    } as unknown as Keycloak;

    TestBed.configureTestingModule({
      providers: [
        AuthService,
        {
          provide: KEYCLOAK_INSTANCE,
          useValue: keycloakMock,
        },
      ],
    });

    service = TestBed.inject(AuthService);
  });

  it('should initialize without an authenticated session', async () => {
    await service.inicializar();

    expect(keycloakMock.init).toHaveBeenCalledOnce();
    expect(service.inicializado()).toBe(true);
    expect(service.autenticado()).toBe(false);
    expect(service.usuario()).toBeNull();
    expect(service.falhaInicializacao()).toBe(false);
  });

  it('should initialize an authenticated session', async () => {
    keycloakMock.authenticated = true;
    keycloakMock.tokenParsed = {
      sub: 'usuario-keycloak-id',
      name: 'Felipe',
      email: 'felipe@email.com',
    };
    keycloakMock.realmAccess = {
      roles: ['VENDEDOR', 'ADMIN'],
    };

    await service.inicializar();

    expect(service.autenticado()).toBe(true);
    expect(service.usuario()).toEqual({
      subject: 'usuario-keycloak-id',
      nome: 'Felipe',
      email: 'felipe@email.com',
      roles: ['VENDEDOR', 'ADMIN'],
    });
    expect(service.possuiRole('VENDEDOR')).toBe(true);
    expect(service.possuiRole('CLIENTE')).toBe(false);
  });

  it('should delegate login to Keycloak', async () => {
    await service.entrar();

    expect(keycloakMock.login).toHaveBeenCalledExactlyOnceWith({
      redirectUri: window.location.href,
    });
  });

  it('should refresh and return a valid token', async () => {
    keycloakMock.authenticated = true;
    keycloakMock.token = 'token-atualizado';
    keycloakMock.tokenParsed = {
      sub: 'usuario-keycloak-id',
      preferred_username: 'felipe',
    };

    const token = await service.obterTokenValido();

    expect(keycloakMock.updateToken)
      .toHaveBeenCalledExactlyOnceWith(30);

    expect(token).toBe('token-atualizado');
    expect(service.autenticado()).toBe(true);
  });

  it('should keep the public application available when Keycloak fails', async () => {
    vi.mocked(keycloakMock.init)
      .mockRejectedValue(new Error('Keycloak indisponível'));

    await service.inicializar();

    expect(service.inicializado()).toBe(true);
    expect(service.autenticado()).toBe(false);
    expect(service.usuario()).toBeNull();
    expect(service.falhaInicializacao()).toBe(true);
  });

  it('should delegate logout to Keycloak', async () => {
    await service.sair();

    expect(keycloakMock.logout)
      .toHaveBeenCalledExactlyOnceWith({
        redirectUri: `${window.location.origin}/home`,
      });
  });
});