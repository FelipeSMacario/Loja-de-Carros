import { TestBed } from '@angular/core/testing';
import {
  ActivatedRouteSnapshot,
  provideRouter,
  Router,
  RouterStateSnapshot,
  UrlTree,
} from '@angular/router';
import { vi } from 'vitest';

import { adminGuard } from './admin-guard';
import { AuthService } from './auth-service';

describe('adminGuard', () => {
  const authServiceMock = {
    possuiRole: vi.fn(),
  };

  let router: Router;

  beforeEach(() => {
    authServiceMock.possuiRole.mockReset();

    TestBed.configureTestingModule({
      providers: [
        provideRouter([]),
        {
          provide: AuthService,
          useValue: authServiceMock,
        },
      ],
    });

    router = TestBed.inject(Router);
  });

  function executarGuard() {
    return TestBed.runInInjectionContext(
      () => adminGuard(
        {} as ActivatedRouteSnapshot,
        {
          url: '/admin/usuarios',
        } as RouterStateSnapshot
      )
    );
  }

  it('should allow an administrator', () => {
    authServiceMock.possuiRole
      .mockReturnValue(true);

    const resultado = executarGuard();

    expect(authServiceMock.possuiRole)
      .toHaveBeenCalledExactlyOnceWith('ADMIN');

    expect(resultado).toBe(true);
  });

  it('should redirect a non-administrator to home', () => {
    authServiceMock.possuiRole
      .mockReturnValue(false);

    const resultado = executarGuard();

    expect(resultado).toBeInstanceOf(UrlTree);

    expect(
      router.serializeUrl(resultado as UrlTree)
    ).toBe('/home');
  });
});