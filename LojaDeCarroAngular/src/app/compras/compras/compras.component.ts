import { HttpParams } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Carro, Page } from 'src/app/models/Carro.model';
import { CarroService } from 'src/app/services/carro.service';
import { take } from 'rxjs/operators';

@Component({
  selector: 'app-compras',
  templateUrl: './compras.component.html',
  styleUrls: ['./compras.component.css'],
})
export class ComprasComponent implements OnInit {
  page: Page;
  carro: Carro[] = [];

  filterCarro: Carro[] = [];
  elementoTotal: number;
  itemPagina: number;
  totalPagina: number;
  marca : string;

  httpParams = new HttpParams();

  constructor(
    private carroService: CarroService,
    private router: Router,
    private activatedRoute : ActivatedRoute
  ) {}

  ngOnInit(): void {

    if(this.router.url.includes("search")){
      this.homeMarca();
    } else{
      this.listarcarros();
    }
    
    
  }

  listarcarros(parametros?: string): void {
    this.carroService
      .teste(parametros)
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

  parametros(e) {
    
    if (e.marca) this.httpParams = this.httpParams.set('marca', e.marca);

    if (e.modelo) this.httpParams = this.httpParams.set('modelo', e.modelo);

    if (e.anoInicio)
      this.httpParams = this.httpParams.set('anoInicio', e.anoInicio);

    if (e.anoFim) this.httpParams = this.httpParams.set('anoFim', e.anoFim);

    if (e.valorInicio)
      this.httpParams = this.httpParams.set('valorInicio', e.valorInicio);

    if (e.valorFim)
      this.httpParams = this.httpParams.set('valorFim', e.valorFim);

    if (e.quilometragem)
      this.httpParams = this.httpParams.set('quilometragem', e.quilometragem);

    this.listarcarros(this.httpParams.toString());
  }

  limpaParametro() {
    this.listarcarros();
  }
  homeMarca(){    
      this.activatedRoute.queryParams.pipe(take(1)).subscribe(
        (queryParams) => {this.httpParams = this.httpParams.set('marca', queryParams["marca"])}
      )
      this.listarcarros(this.httpParams.toString());
    
  }
}
