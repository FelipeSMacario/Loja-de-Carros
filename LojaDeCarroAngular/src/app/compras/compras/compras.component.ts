import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Carro } from 'src/app/models/Carro.model';
import { CarroService } from 'src/app/services/carro.service';



@Component({
  selector: 'app-compras',
  templateUrl: './compras.component.html',
  styleUrls: ['./compras.component.css'],
})
export class ComprasComponent implements OnInit {
  carro: Carro[] = [];
  filterCarro: Carro[];
  marca: string;
  modelo : string;
  valorInicio : number;
  valorFinal : number;
  anoInicio : number;
  anoFim : number;
  quilometragem : number;

  constructor(
    private carroService: CarroService,
    private router: Router,
    private activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.marca = this.activatedRoute.snapshot.params['Marca'];
    this.modelo = this.activatedRoute.snapshot.params['Modelo'];
    this.valorInicio = this.activatedRoute.snapshot.params['valor1'];
    this.valorFinal = this.activatedRoute.snapshot.params['valor2'];
    this.anoInicio = this.activatedRoute.snapshot.params['KM1'];
    this.anoFim = this.activatedRoute.snapshot.params['KM2'];
    this.quilometragem = this.activatedRoute.snapshot.params['valor'];

    if (this.marca && this.modelo == undefined) {

      const valorAsync = new Promise((resolve, reject) => {
        setTimeout(() => resolve( this.filtrarMarca(this.marca)), 50)
      });    
      
    } else if(this.marca && this.modelo) {
      const valorAsync = new Promise((resolve, reject) => {
        setTimeout(() => resolve( this.filtrarModelo(this.marca, this.modelo)), 50)
      });   
    }
    if (this.anoInicio && this.anoFim){
      const valorAsync = new Promise((resolve, reject) => {
        setTimeout(() => resolve( this.filtrarAno(this.anoInicio, this.anoFim)), 50)
      });   
    }
    if (this.valorInicio && this.valorFinal){
      const valorAsync = new Promise((resolve, reject) => {
        setTimeout(() => resolve( this.filtrarValor(this.valorInicio, this.valorFinal)), 50)
      });   
    }    
    if (this.quilometragem){
      const valorAsync = new Promise((resolve, reject) => {
        setTimeout(() => resolve( this.filtrarQuilometragem(this.quilometragem)), 50)
      });   
    } 

    this.listarcarros();
  }

  listarcarros(): void {
    this.carroService.findAllCarros().subscribe({
      next: (car) => {
        this.carro = car;
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

  filtrarModelo(marca : string, modelo : string) : void {
    this.carroService.findByModelo(marca, modelo).subscribe({
      next: car => {this.filterCarro = car; this.carro = this.filterCarro},
      error : err => console.log(err)
    })
  }

  filtrarAno(anoInicio : number, anoFim : number) : void {
    this.carroService.findByAno(anoInicio, anoFim).subscribe({
      next: car => {this.filterCarro = car; this.carro = this.filterCarro},
      error : err => console.log(err)
    })
  }

  filtrarValor(valorInicio : number, valorFinal : number) : void {
    this.carroService.findByValor(valorInicio, valorFinal).subscribe({
      next: car => {this.filterCarro = car; this.carro = this.filterCarro},
      error : err => console.log(err)
    })
  }

  filtrarQuilometragem(valor : number) : void {
    this.carroService.findByQuilometragem(valor).subscribe({
      next: car => {this.filterCarro = car; this.carro = this.filterCarro},
      error : err => console.log(err)
    })
  }

}
