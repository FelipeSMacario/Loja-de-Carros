import { HttpParams } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { take } from 'rxjs/operators';
import { LoginService } from 'src/app/services/login.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  cadastro : FormGroup;

  constructor(
    private loginService : LoginService,
    private fb : FormBuilder
  ) { }

  ngOnInit(): void {
    this.cadastro = this.fb.group({
      login : [null],
      password : [null]
    })
  }

  cliquei(){
    const params = new HttpParams()
    .set("login", this.cadastro.value.login)
    .set("password", this.cadastro.value.password);

   this.loginService.logar(params.toString()).pipe(take(1)).subscribe({
     next : result => console.log(result),
     error : err => console.log(err)
   })

   
  }

}
