import { HttpParams } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Carro, Page } from 'src/app/models/Carro.model';
import { CarroService } from 'src/app/services/carro.service';
import { distinctUntilChanged, take } from 'rxjs/operators';

@Component({
  selector: 'app-compras',
  templateUrl: './compras.component.html',
  styleUrls: ['./compras.component.css'],
})
export class ComprasComponent implements OnInit {
  page: Page;
  carro: Carro[] = [];
  imgdefault : string = "../../../assets/images/semImagem.jpg"
  filterCarro: Carro[] = [];
  elementoTotal: number;
  itemPagina: number;
  totalPagina: number;
  marca: string;

  httpParams = new HttpParams();

  constructor(
    private carroService: CarroService,
    private router: Router,
    private activatedRoute: ActivatedRoute
  ) {
  
  }

  ngOnInit(): void {
      

    this.atualizaParametros();
  }

  listarcarros(parametros?: string): void {
    this.carroService
      .findAllCarros(parametros)
      .pipe(take(1))
      .subscribe({
        next: (car) => {
          this.page = car;
          this.carro = car.content;
          this.elementoTotal = car.totalElements;
          this.itemPagina = car.numberOfElements;
          this.totalPagina = car.totalPages;
        },
        error: (err) => console.log(err),
      });
  }

  digitaValor(id: number) {
    this.router.navigate(['compras/detalhes', id]);
  }

  atualizaPagina(e) {
    this.httpParams = this.httpParams.set('page', e - 1);
    this.listarcarros(this.httpParams.toString());
  }

  atualizaParametros(){
    this.activatedRoute.queryParams.pipe( distinctUntilChanged()).subscribe((queryParams) => {
      if(queryParams["marca"]) {
        this.httpParams = this.httpParams.set("marca", queryParams["marca"]);
      }
      if(queryParams["modelo"]) {
        this.httpParams = this.httpParams.set("modelo", queryParams["modelo"]);
      }
      if(queryParams["anoInicio"]) {
        this.httpParams = this.httpParams.set("anoInicio", queryParams["anoInicio"]);
      }
      if(queryParams["anoFim"]) {
        this.httpParams = this.httpParams.set("anoFim", queryParams["anoFim"]);
      }
      if(queryParams["valorInicio"]) {
        this.httpParams = this.httpParams.set("valorInicio", queryParams["valorInicio"]);
      }
      if(queryParams["valorFim"]) {
        this.httpParams = this.httpParams.set("valorFim", queryParams["valorFim"]);
      }  
      if(queryParams["quilometragem"]) {
        this.httpParams = this.httpParams.set("quilometragem", queryParams["quilometragem"]);
      }  
      if(queryParams["page"]) {
        this.httpParams = this.httpParams.set("page", queryParams["page"] - 1);
      } 
      this.listarcarros(this.httpParams.toString())
    })
  }


}

