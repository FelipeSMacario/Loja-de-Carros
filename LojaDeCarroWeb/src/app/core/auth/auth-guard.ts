import { inject } from '@angular/core';
import { CanActivateFn } from '@angular/router';

import { AuthService } from './auth-service';

export const authGuard: CanActivateFn =
  (_route, state) => {
    const authService = inject(AuthService);

    if (authService.autenticado()) {
      return true;
    }

    const redirectUri = new URL(
      state.url,
      window.location.origin
    ).href;

    void authService.entrar(redirectUri);

    return false;
  };