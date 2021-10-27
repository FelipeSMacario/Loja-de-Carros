import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ComprasComponent } from './compras/compras.component';
import { HomeComponent } from './home/home.component';
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
  path : "vendas",
  component : ListarVendasComponent
}];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
