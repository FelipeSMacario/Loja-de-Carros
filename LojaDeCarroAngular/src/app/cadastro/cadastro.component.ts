import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Usuario } from '../models/usuario.model';
import { UsuarioService } from '../services/usuario.service';

@Component({
  selector: 'app-cadastro',
  templateUrl: './cadastro.component.html',
  styleUrls: ['./cadastro.component.css']
})
export class CadastroComponent implements OnInit {

  usuario : Usuario = new Usuario();
  usuarios : Usuario[] = [];
  formulario : FormGroup;
  file : FileList;

  constructor(
    private fb : FormBuilder,
    private usuarioService : UsuarioService
  ) { }

  ngOnInit(): void {
    this.formulario = this.fb.group({
      cpf : [null],
      dtNascimento : [null],
      email : [null],
      nome : [null],
      url : [null]
    })
  }

  cliquei(){
    this.file = this.formulario.value.url;
    console.log(this.file)
    console.log(this.formulario.value)
  }

}
