import { Routes } from '@angular/router';
import { Shell } from './layout/shell/shell';
import { authGuard } from './core/auth/auth-guard';

export const routes: Routes = [
  {
    path: '',
    component: Shell,
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'home',
      },
      {
        path: 'home',
        loadComponent: () =>
          import('./features/home/pages/home/home')
            .then(component => component.Home),
      },
      {
        path: 'veiculos/anunciar',
        canActivate: [authGuard],
        loadComponent: () =>
          import(
            './features/veiculos/pages/veiculo-cadastro/veiculo-cadastro'
          ).then(component => component.VeiculoCadastro),
      },
      {
        path: 'veiculos/meus-anuncios',
        canActivate: [authGuard],
        loadComponent: () =>
          import(
            './features/veiculos/pages/meus-anuncios/meus-anuncios'
          ).then(component => component.MeusAnuncios),
      },
      {
        path: 'veiculos/meus-anuncios/:id/editar',
        canActivate: [authGuard],
        data: {
          modoEdicao: true,
        },
        loadComponent: () =>
          import(
            './features/veiculos/pages/veiculo-cadastro/veiculo-cadastro'
          ).then(component => component.VeiculoCadastro),
      },
      {
        path: 'veiculos/meus-anuncios/:id',
        canActivate: [authGuard],
        data: {
          meuAnuncio: true,
        },
        loadComponent: () =>
          import(
            './features/veiculos/pages/veiculo-detalhe/veiculo-detalhe'
          ).then(component => component.VeiculoDetalhe),
      },
      {
        path: 'veiculos/:id',
        loadComponent: () =>
          import(
            './features/veiculos/pages/veiculo-detalhe/veiculo-detalhe'
          ).then(component => component.VeiculoDetalhe),
      },
    ],
  },
  {
    path: '**',
    redirectTo: 'home',
  },
];