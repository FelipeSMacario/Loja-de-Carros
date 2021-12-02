import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Marca } from '../models/marca.model';
import { Usuario } from '../models/usuario.model';
import { MarcasService } from '../services/marcas.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  marca : Marca[] = [];
  usuario : Usuario = new Usuario();
  usuarioAutenticado : boolean;

  constructor(
    private marcaService : MarcasService,
    private router : Router
  ) { }

  ngOnInit(): void {
    this.listarMarcas();
    this.usuario = JSON.parse(localStorage.getItem("usuario")!);
    this.usuarioAutenticado = JSON.parse(localStorage.getItem("usuarioAutenticado")!);

    this.usuarioAutenticado ? this.usuarioAutenticado = true : this.usuarioAutenticado = false;
    
  }

  listarMarcas() : void {
      this.marcaService.findAllMarcas().subscribe({
      next : (marca) => (this.marca = marca),
      error : (err) => console.log(err)
    })
  }
  selecionaMarca(marca : string){
    this.router.navigate(["/compras/search/"], {queryParams : {"marca" : marca}})
  }
}
