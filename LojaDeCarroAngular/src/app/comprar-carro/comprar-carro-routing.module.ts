import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ComprarCarroComponent } from './comprar-carro.component';

const routes: Routes = [
  {
    path: ":id",
    component : ComprarCarroComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ComprarCarroRoutingModule { }
