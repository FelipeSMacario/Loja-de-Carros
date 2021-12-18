import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from './guards/auth.guard';
import { ChildGuard } from './guards/child.guard';

const routes: Routes = [
  {
    path: '',
    redirectTo: 'home',
    pathMatch: 'full',
  },
  {
    path : "cadastro",
    loadChildren: () => import("./cadastro/cadastro.module").then(m => m.CadastroModule),
    
  },

  {
    path : "login",
    loadChildren: () => import("./Login/login.module").then(m => m.LoginModule)
  },

  {
    path : "compras",
    loadChildren: () => import("./compras/compras.module").then(m => m.ComprasModule),
  },

  {
    path : "carro",
    loadChildren: () => import("./carro/carro.module").then(m => m.CarroModule),
    
  },
  {
    path : "home",
    loadChildren: () => import("./home/home.module").then(m => m.HomeModule)
  },
  {
    path : "vendas",
    loadChildren: () => import("./vendas/vendas.module").then(m => m.VendasModule),
    canActivate : [AuthGuard],
    canActivateChild : [ChildGuard]
  },
  {
    path : "comprar",
    loadChildren: () => import("./comprar-carro/comprar-carro.module").then(m => m.ComprarCarroModule),
    canActivate : [AuthGuard]
  }
  
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],

  exports: [RouterModule],
})
export class AppRoutingModule {}
