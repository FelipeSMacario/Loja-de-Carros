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