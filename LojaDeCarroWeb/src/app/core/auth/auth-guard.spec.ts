import {
  signal,
} from '@angular/core';
import {
  TestBed,
} from '@angular/core/testing';
import {
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
} from '@angular/router';
import { vi } from 'vitest';

import { AuthService } from './auth-service';
import { authGuard } from './auth-guard';

describe('authGuard', () => {
  const autenticado = signal(false);

  const authServiceMock = {
    autenticado,
    entrar: vi.fn().mockResolvedValue(undefined),
  };

  beforeEach(() => {
    autenticado.set(false);
    authServiceMock.entrar.mockClear();

    TestBed.configureTestingModule({
      providers: [
        {
          provide: AuthService,
          useValue: authServiceMock,
        },
      ],
    });
  });

  function executarGuard(
    url = '/veiculos/anunciar'
  ): boolean {
    return TestBed.runInInjectionContext(() =>
      authGuard(
        {} as unknown as ActivatedRouteSnapshot,
        { url } as RouterStateSnapshot
      )
    ) as boolean;
  }

  it('should allow an authenticated user', () => {
    autenticado.set(true);

    const resultado = executarGuard();

    expect(resultado).toBe(true);
    expect(authServiceMock.entrar)
      .not.toHaveBeenCalled();
  });

  it('should redirect a visitor to login', () => {
    const resultado = executarGuard();

    expect(resultado).toBe(false);

    expect(authServiceMock.entrar)
      .toHaveBeenCalledExactlyOnceWith(
        `${window.location.origin}/veiculos/anunciar`
      );
  });
});