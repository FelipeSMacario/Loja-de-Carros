import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { take } from 'rxjs/operators';
import { Usuario } from 'src/app/models/usuario.model';
import { UsuarioService } from 'src/app/services/usuario.service';


@Component({
  selector: 'app-cadastro',
  templateUrl: './cadastro.component.html',
  styleUrls: ['./cadastro.component.css']
})
export class CadastroComponent implements OnInit {

  usuario : Usuario = new Usuario();
  usuarios : Usuario[] = [];
  formulario : FormGroup;

  constructor(
    private fb : FormBuilder,
    private usuarioService : UsuarioService
  ) { }

  ngOnInit(): void {
    this.formulario = this.fb.group({
      id : [null],
      cpf : [null],
      dtNascimento : [null],
      email : [null],
      nome : [null],
    })
  }
  salvarUsuario(){
    this.usuarioService.saveUsuario(this.formulario.value).pipe(take(1)).subscribe({
      next : user => console.log("Cadastrado com sucesso", user),
      error : err => console.log(err)
    })
  }

}
