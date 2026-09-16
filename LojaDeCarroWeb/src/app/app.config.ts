import {
  ApplicationConfig,
  inject,
  provideAppInitializer,
  provideBrowserGlobalErrorListeners,
} from '@angular/core';
import {provideHttpClient, withInterceptors,} from '@angular/common/http';

import { authInterceptor } from './core/auth/auth-interceptor';
import { provideRouter } from '@angular/router';

import { AuthService } from './core/auth/auth-service';
import { routes } from './app.routes';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
    provideHttpClient(
      withInterceptors([authInterceptor])
    ),

    provideAppInitializer(() =>
      inject(AuthService).inicializar()
    ),
  ],
};