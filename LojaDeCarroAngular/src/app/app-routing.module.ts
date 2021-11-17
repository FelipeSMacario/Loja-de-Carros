import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CarroComponent } from './carro/carro/carro.component';
import { ComprasComponent } from './compras/compras/compras.component';
import { HomeComponent } from './home/home.component';
import { ListarVendasComponent } from './vendas/listar-vendas/listar-vendas.component';

const routes: Routes = [
  {
    path : "", redirectTo: 'home', pathMatch: 'full'
  },

  {
    path : "home",
    component : HomeComponent
  },
  {
    path : "compras",
    component : ComprasComponent
  },

  {
    path : "compras/search",
    component : ComprasComponent
  },

  {
    path : "compras/detalhes/:id",
    component : CarroComponent
  },
  {
  path : "vendas",
  component : ListarVendasComponent
}];

@NgModule({ 
  imports: [RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload' }), ],
  
  exports: [RouterModule]
})
export class AppRoutingModule { }
