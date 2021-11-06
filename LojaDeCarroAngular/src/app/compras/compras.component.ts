import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Carro } from '../models/Carro.model';
import { CarroService } from '../services/carro.service';

@Component({
  selector: 'app-compras',
  templateUrl: './compras.component.html',
  styleUrls: ['./compras.component.css'],
})
export class ComprasComponent implements OnInit {
  carro: Carro[] = [];
  filterCarro: Carro[];
  marca: string;

  constructor(
    private carroService: CarroService,
    private router: Router,
    private activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.marca = this.activatedRoute.snapshot.params['Marca'];

    if (this.marca) {

      const valorAsync = new Promise((resolve, reject) => {
        setTimeout(() => resolve( this.filtrarMarca(this.marca)), 50)
      });


     
      
    }

    this.listarcarros();
  }

  listarcarros(): void {
    this.carroService.findAllCarros().subscribe({
      next: (car) => {
        this.carro = car;
        console.log("XINGA LA")
      },
      error: (err) => console.log(err),
    });
  }

  digitaValor(id: number) {
    this.router.navigate(['compras/detalhes', id]);
  }

  filtrarMarca(marca: string): void {
    this.carroService.findByMarca(marca).subscribe({
      next: (car) => {this.filterCarro = car; this.carro = this.filterCarro;},
      error: (err) => console.log(err),
    });
  }

  falaAi() {
    console.log(this.carro);
    console.log(this.filterCarro);
    console.log(this.marca);
  }
}
