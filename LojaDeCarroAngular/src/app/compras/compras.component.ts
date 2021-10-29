import { Component, OnInit } from '@angular/core';
import { Carro } from '../models/Carro.model';
import { CarroService } from '../services/carro.service';

@Component({
  selector: 'app-compras',
  templateUrl: './compras.component.html',
  styleUrls: ['./compras.component.css']
})
export class ComprasComponent implements OnInit {

  carro : Carro[] = [];

  constructor(private carroService : CarroService) { }

  ngOnInit(): void {
    this.listarcarros();
  }

  listarcarros() : void {
    this.carroService.findAllCarros().subscribe({
      next : (car) => (this.carro = car),
      error : (err) => (console.log(err))
    })
  }
  
  digitaValor(id : number) {
    console.log(id)
  }
}
