import { Component, OnInit } from '@angular/core';
import { Usuario } from 'src/app/models/usuario.model';

@Component({
  selector: 'app-nav-bar',
  templateUrl: './nav-bar.component.html',
  styleUrls: ['./nav-bar.component.css']
})
export class NavBarComponent implements OnInit {

  usuario : Usuario = new Usuario();
  usuarioAutenticado : boolean;

  constructor() { }

  ngOnInit(): void {
    this.usuario = JSON.parse(localStorage.getItem("usuario")!);
    this.usuarioAutenticado = JSON.parse(localStorage.getItem("usuarioAutenticado")!);

    this.usuarioAutenticado ? this.usuarioAutenticado = true : this.usuarioAutenticado = false;
  }

  logout(){
    localStorage.clear();
    this.usuarioAutenticado = false;
    window.location.reload();
  }
  
}
