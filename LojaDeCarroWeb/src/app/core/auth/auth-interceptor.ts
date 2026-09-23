import { inject } from '@angular/core';
import {
  HttpInterceptorFn,
} from '@angular/common/http';
import { from, switchMap } from 'rxjs';

import { environment } from '../../../environments/environment';
import { AuthService } from './auth-service';

export const authInterceptor: HttpInterceptorFn =
  (request, next) => {
    const ehRequisicaoDaApi =
      request.url === environment.apiUrl
      || request.url.startsWith(`${environment.apiUrl}/`);

    if (!ehRequisicaoDaApi) {
      return next(request);
    }

    const authService = inject(AuthService);

    return from(authService.obterTokenValido()).pipe(
      switchMap(token => {
        if (token === null) {
          return next(request);
        }

        const requestAutenticada = request.clone({
          setHeaders: {
            Authorization: `Bearer ${token}`,
          },
        });

        return next(requestAutenticada);
      })
    );
  };