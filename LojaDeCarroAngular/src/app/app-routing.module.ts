import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CarroComponent } from './carro/carro.component';
import { ComprasComponent } from './compras/compras.component';
import { HomeComponent } from './home/home.component';
import { Carro } from './models/Carro.model';
import { ListarVendasComponent } from './vendas/listar-vendas/listar-vendas.component';

const routes: Routes = [
  {
    path : "home",
    component : HomeComponent
  },
  {
    path : "compras",
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
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
