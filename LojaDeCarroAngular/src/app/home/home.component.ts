import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Marca } from '../models/marca.model';
import { MarcasService } from '../services/marcas.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  marca : Marca[] = [];

  constructor(
    private marcaService : MarcasService,
    private router : Router) { }

  ngOnInit(): void {
    this.listarMarcas();
  }

  listarMarcas() : void {
      this.marcaService.findAllMarcas().subscribe({
      next : (marca) => (this.marca = marca),
      error : (err) => console.log(err)
    })
  }

  digitaValor(nome : string){
    this.router.navigate(['compras/Marca/',nome]);
  }
}
