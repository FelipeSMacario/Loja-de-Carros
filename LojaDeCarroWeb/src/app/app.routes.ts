import { Routes } from '@angular/router';
import { Shell } from './layout/shell/shell';

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