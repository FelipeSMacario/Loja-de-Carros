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
import {MatFormFieldModule} from '@angular/material/form-field';
import { UploadArquivosComponent } from './carro/upload-arquivos/upload-arquivos.component';
import { ListarKitComponent } from './vendas/listar-kit/listar-kit.component';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatNativeDateModule} from '@angular/material/core';
import { MatInputModule } from '@angular/material/input';
import { DatePickerComponent } from './shared/angular-form/date-picker/date-picker.component';
import { EMailComponent } from './shared/angular-form/e-mail/e-mail.component';
import { InputTextComponent } from './shared/angular-form/input-text/input-text.component';
import { InputSelectComponent } from './shared/angular-form/input-select/input-select.component';
import { InputNumberComponent } from './shared/angular-form/input-number/input-number.component';
import { LoginComponent } from './cadastro/login/login.component';
import { CadastroComponent } from './cadastro/cadastro/cadastro.component';
import { InputPasswordComponent } from './shared/angular-form/input-password/input-password.component';
import { BsDropdownModule } from 'ngx-bootstrap/dropdown';
import { ModalComponent } from './shared/modal/modal.component';
import { AlertModalComponent } from './shared/modal/alert-modal/alert-modal.component';
import { BsModalService } from 'ngx-bootstrap/modal';

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
    CadastroComponent,
    UploadArquivosComponent,
    ListarKitComponent,
    DatePickerComponent,
    EMailComponent,
    InputTextComponent,
    InputSelectComponent,
    InputNumberComponent,
    LoginComponent,
    InputPasswordComponent,
    ModalComponent,
    AlertModalComponent,

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
    FormsModule,
    MatFormFieldModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatInputModule,
    BsDropdownModule.forRoot(),
  ],
  
  providers: [BsModalService],
  bootstrap: [AppComponent]
})
export class AppModule { }
