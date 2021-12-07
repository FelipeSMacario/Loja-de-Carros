import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CadastroComponent } from './cadastro/cadastro/cadastro.component';
import { LoginComponent } from './cadastro/login/login.component';

import { CarroComponent } from './carro/carro/carro.component';
import { ComprasComponent } from './compras/compras/compras.component';
import { HomeComponent } from './home/home.component';
import { ListarVendasComponent } from './vendas/listar-vendas/listar-vendas.component';

const routes: Routes = [
  {
    path: '',
    redirectTo: 'home',
    pathMatch: 'full',
  },

  {
    path: 'home',
    component: HomeComponent,
  },
  {
    path: 'compras',
    component: ComprasComponent,
  },

  {
    path: 'compras/search',
    component: ComprasComponent,
  },

  {
    path: 'compras/detalhes/:id',
    component: CarroComponent,
  },
  {
    path: 'vendas',
    component: ListarVendasComponent,
  },
  {
    path: 'vendas/:id',
    component: ListarVendasComponent,
  },

  {
    path: 'cadastro',
    component: CadastroComponent,
  },
  {
    path: 'login',
    component: LoginComponent,
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],

  exports: [RouterModule],
})
export class AppRoutingModule {}
