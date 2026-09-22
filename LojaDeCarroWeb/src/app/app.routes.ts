import { Routes } from '@angular/router';
import { Shell } from './layout/shell/shell';
import { authGuard } from './core/auth/auth-guard';
import {
  cadastroPendenteGuard,
  contaDesativadaGuard,
  perfilCompletoGuard,
} from './features/usuarios/guards/perfil-guards';
import {
  adminGuard,
} from './core/auth/admin-guard';
export const routes: Routes = [
  {
    path: '',
    component: Shell,
    children: [
      {
        path: 'admin/usuarios',
        canActivate: [
          authGuard,
          perfilCompletoGuard,
          adminGuard,
        ],
        loadComponent: () =>
          import(
            './features/usuarios/pages/admin-usuarios/admin-usuarios'
          ).then(component => component.AdminUsuarios),
      },
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
        path: 'completar-cadastro',
        canActivate: [
          authGuard,
          cadastroPendenteGuard,
        ],
        loadComponent: () =>
          import(
            './features/usuarios/pages/completar-cadastro/completar-cadastro'
          ).then(component => component.CompletarCadastro),
      },
      {
        path: 'veiculos/anunciar',
        canActivate: [
          authGuard,
          perfilCompletoGuard,
        ],
        loadComponent: () =>
          import(
            './features/veiculos/pages/veiculo-cadastro/veiculo-cadastro'
          ).then(component => component.VeiculoCadastro),
      },
      {
        path: 'veiculos/meus-anuncios',
        canActivate: [
          authGuard,
          perfilCompletoGuard,
        ],
        loadComponent: () =>
          import(
            './features/veiculos/pages/meus-anuncios/meus-anuncios'
          ).then(component => component.MeusAnuncios),
      },
      {
        path: 'vendas/minhas-compras',
        canActivate: [
          authGuard,
          perfilCompletoGuard,
        ],
        loadComponent: () =>
          import(
            './features/veiculos/pages/minhas-compras/minhas-compras'
          ).then(component => component.MinhasCompras),
      },
      {
        path: 'vendas/minhas-vendas',
        canActivate: [
          authGuard,
          perfilCompletoGuard,
        ],
        loadComponent: () =>
          import(
            './features/veiculos/pages/minhas-vendas/minhas-vendas'
          ).then(component => component.MinhasVendas),
      },
      {
        path: 'veiculos/meus-anuncios/:id/editar',
        canActivate: [
          authGuard,
          perfilCompletoGuard,
        ],
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
        canActivate: [
          authGuard,
          perfilCompletoGuard,
        ],
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
      {
        path: 'conta-desativada',
        canActivate: [
          authGuard,
          contaDesativadaGuard,
        ],
        loadComponent: () =>
          import(
            './features/usuarios/pages/conta-desativada/conta-desativada'
          ).then(component => component.ContaDesativada),
      },
      {
        path: 'conta',
        canActivate: [
          authGuard,
          perfilCompletoGuard,
        ],
        loadComponent: () =>
          import(
            './features/usuarios/pages/minha-conta/minha-conta'
          ).then(component => component.MinhaConta),
      },
    ],
  },
  {
    path: '**',
    redirectTo: 'home',
  },
];