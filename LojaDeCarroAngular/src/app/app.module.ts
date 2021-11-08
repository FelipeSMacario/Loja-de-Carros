import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { NavBarComponent } from './shared/nav-bar/nav-bar.component';
import { SideBarComponent } from './shared/side-bar/side-bar.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {MatSidenavModule} from '@angular/material/sidenav';
import { ListarVendasComponent } from './vendas/listar-vendas/listar-vendas.component';
import { FooterComponent } from './shared/footer/footer.component';
import { HomeComponent } from './home/home.component';
import { HttpClientModule } from '@angular/common/http';
import { FiltroComponent } from './compras/filtro/filtro.component';
import { ReactiveFormsModule } from '@angular/forms';
import {MatSelectModule} from '@angular/material/select';
import { RangeComponent } from './shared/range/range.component';
import { Ng5SliderModule } from 'ng5-slider';
import { RangeSimplesComponent } from './shared/range-simples/range-simples.component';
import { DetalhesComponent } from './carro/detalhes/detalhes.component';
import { TabsModule } from 'ngx-bootstrap/tabs';
import { CarouselModule } from 'ngx-bootstrap/carousel';
import {MatIconModule} from '@angular/material/icon';
import { ComprasComponent } from './compras/compras/compras.component';
import { CarroComponent } from './carro/carro/carro.component';
import { PaginationModule } from 'ngx-bootstrap/pagination';
import {MatSliderModule} from '@angular/material/slider';
import { PaginacaoComponent } from './shared/paginacao/paginacao.component';
import { FormsModule } from '@angular/forms';

@NgModule({
  declarations: [
    AppComponent,
    NavBarComponent,
    SideBarComponent,
    ListarVendasComponent,
    FooterComponent,
    HomeComponent,
    ComprasComponent,
    FiltroComponent,
    RangeComponent,
    RangeSimplesComponent,
    CarroComponent,
    DetalhesComponent,
    PaginacaoComponent,

  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    MatSidenavModule,
    HttpClientModule,
    ReactiveFormsModule,
    MatSelectModule,
    Ng5SliderModule,
    TabsModule,
    CarouselModule,
    MatIconModule,
    MatSliderModule,
    PaginationModule,
    FormsModule
    
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
