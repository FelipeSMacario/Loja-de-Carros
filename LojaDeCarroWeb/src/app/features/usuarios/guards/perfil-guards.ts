import { inject } from '@angular/core';
import {
  CanActivateFn,
  Router,
} from '@angular/router';
import {
  catchError,
  map,
  of,
} from 'rxjs';

import {
  perfilUsuarioPendente,
  UsuarioApi,
  UsuarioAtualResponse,
} from '../data-access/usuario-api';

function destinoPerfilExistente(
  usuario: UsuarioAtualResponse,
  router: Router
): true | ReturnType<Router['createUrlTree']> {
  return usuario.ativo
    ? true
    : router.createUrlTree(['/conta-desativada']);
}

export const perfilCompletoGuard: CanActivateFn = (
  _route,
  state
) => {
  const usuarioApi = inject(UsuarioApi);
  const router = inject(Router);

  const usuarioAtual = usuarioApi.usuarioAtual();

  if (usuarioAtual) {
    return destinoPerfilExistente(
      usuarioAtual,
      router
    );
  }

  return usuarioApi.buscarAtual().pipe(
    map(usuario =>
      destinoPerfilExistente(usuario, router)
    ),
    catchError(erro => {
      if (!perfilUsuarioPendente(erro)) {
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

  const redirecionarPerfilExistente = (
    usuario: UsuarioAtualResponse
  ) => router.createUrlTree([
    usuario.ativo
      ? '/home'
      : '/conta-desativada',
  ]);

  const usuarioAtual = usuarioApi.usuarioAtual();

  if (usuarioAtual) {
    return redirecionarPerfilExistente(
      usuarioAtual
    );
  }

  return usuarioApi.buscarAtual().pipe(
    map(usuario =>
      redirecionarPerfilExistente(usuario)
    ),
    catchError(erro => {
      if (perfilUsuarioPendente(erro)) {
        return of(true);
      }

      return of(true);
    })
  );
};

export const contaDesativadaGuard: CanActivateFn = (
  _route,
  state
) => {
  const usuarioApi = inject(UsuarioApi);
  const router = inject(Router);

  const usuarioAtual = usuarioApi.usuarioAtual();

  if (usuarioAtual) {
    return usuarioAtual.ativo
      ? router.createUrlTree(['/home'])
      : true;
  }

  return usuarioApi.buscarAtual().pipe(
    map(usuario =>
      usuario.ativo
        ? router.createUrlTree(['/home'])
        : true
    ),
    catchError(erro => {
      if (perfilUsuarioPendente(erro)) {
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
      }

      return of(router.createUrlTree(['/home']));
    })
  );
};