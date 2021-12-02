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
  usuarioStorage : any = "Usuário";

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
   this.loginService.login(this.cadastro.value.login, this.cadastro.value.password).subscribe({
     next : data => {
       const params = new HttpParams().set("login", this.cadastro.value.login).set("password", this.cadastro.value.password);
       console.log(params.toString())
       this.loginService.getUser(params.toString()).pipe(take(1)).subscribe({
         next : user => this.usuario = user
       })
     },
     error : err => console.log(err)
   })
  }

}
