import { HttpParams } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { take } from 'rxjs/operators';
import { Usuario } from 'src/app/models/usuario.model';
import { LoginService } from 'src/app/services/login.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  usuario : Usuario = new Usuario();
  usuarioAutenticado : boolean = false;

  cadastro : FormGroup;

  constructor(
    private loginService : LoginService,
    private fb : FormBuilder,
  ) { }

  ngOnInit(): void {
    this.cadastro = this.fb.group({
      login : [null],
      password : [null]
    })
  }

  entrar(){
    console.log(this.cadastro.value.login, this.cadastro.value.password)
   let resp = this.loginService.logar(this.cadastro.value.login, this.cadastro.value.password);
    resp.subscribe(data => console.log(data))
  }

}
