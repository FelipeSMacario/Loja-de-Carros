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
    path : "compras/Marca/:Marca",
    component : ComprasComponent
  },
  {
    path : "compras/Marca/:Marca/Modelo/:Modelo",
    component : ComprasComponent
  },

  {
    path : "compras/AnoCarro/:KM1/:KM2",
    component : ComprasComponent
  },

  {
    path : "compras/Quilometragem/:valor",
    component : ComprasComponent
  },

  {
    path : "compras/Valor/:valor1/:valor2",
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
