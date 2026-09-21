import { inject } from '@angular/core';
import {  CanActivateFn,  Router,} from '@angular/router';
import {catchError, map,  of,} from 'rxjs';

import {perfilUsuarioPendente, UsuarioApi,} from '../data-access/usuario-api';

export const perfilCompletoGuard: CanActivateFn = (
  _route,
  state
) => {
  const usuarioApi = inject(UsuarioApi);
  const router = inject(Router);

  if (usuarioApi.usuarioAtual()) {
    return true;
  }

  return usuarioApi.buscarAtual().pipe(
    map(() => true),
    catchError(erro => {
      if (!perfilUsuarioPendente(erro)) {
        // O guard organiza o onboarding, mas não substitui
        // o tratamento de indisponibilidade de cada página.
        return of(true);
      }

      return of(
        router.createUrlTree(
          ['/completar-cadastro'],
          {
            queryParams: {
              returnUrl: state.url,
            },
          }
        )
      );
    })
  );
};

export const cadastroPendenteGuard: CanActivateFn = () => {
  const usuarioApi = inject(UsuarioApi);
  const router = inject(Router);

  if (usuarioApi.usuarioAtual()) {
    return router.createUrlTree(['/home']);
  }

  return usuarioApi.buscarAtual().pipe(
    map(() =>
      router.createUrlTree(['/home'])
    ),
    catchError(erro => {
      if (perfilUsuarioPendente(erro)) {
        return of(true);
      }

      // Se a verificação falhar por indisponibilidade,
      // a própria página exibirá o erro ao enviar.
      return of(true);
    })
  );
};