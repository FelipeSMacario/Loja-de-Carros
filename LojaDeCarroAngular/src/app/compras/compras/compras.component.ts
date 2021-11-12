import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Carro, Page } from 'src/app/models/Carro.model';
import { CarroService } from 'src/app/services/carro.service';


@Component({
  selector: 'app-compras',
  templateUrl: './compras.component.html',
  styleUrls: ['./compras.component.css'],
})


export class ComprasComponent implements OnInit {
  page : Page;
  carro: Carro[] = [];
  filterCarro: Carro[];
  elementoTotal : number;
  itemPagina : number;

  constructor(
    private carroService: CarroService,
    private router: Router,
    private activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {

    this.listarcarros();
  }

  listarcarros(): void {
    this.carroService.findAllCarros().subscribe({
      next: (car) => {
        this.page = car;
        this.carro = car.content;
        this.elementoTotal = car.totalElements;
        this.itemPagina = car.numberOfElements;
        
      },
      error: (err) => console.log(err),
    });
  }

  digitaValor(id: number) {
    this.router.navigate(['compras/detalhes', id]);
  }

  atualizaPagina(e){
    this.carroService.findAllCarros(e - 1).subscribe({
      next : car => this.carro = car.content,
      error : err => console.log(err)
    })
  }

}
