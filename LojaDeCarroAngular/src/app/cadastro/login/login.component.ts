import { HttpParams } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { take } from 'rxjs/operators';
import { Usuario } from 'src/app/models/usuario.model';
import { LoginService } from 'src/app/services/login.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent implements OnInit {
  usuarioStorage: Usuario = new Usuario();

  cadastro: FormGroup;

  constructor(
    private loginService: LoginService,
    private fb: FormBuilder,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.cadastro = this.fb.group({
      login: [null],
      password: [null],
    });
  }

  entrar() {
    this.loginService
      .login(this.cadastro.value.login, this.cadastro.value.password)
      .pipe(take(1))
      .subscribe({
        next: (data) => {
          const params = new HttpParams()
            .set('login', this.cadastro.value.login)
            .set('password', this.cadastro.value.password);

          this.loginService
            .getUser(params.toString())
            .pipe(take(1))
            .subscribe({
              next: (user) => {
                localStorage.setItem('usuario', JSON.stringify(user));
                localStorage.setItem(
                  'usuarioAutenticado',
                  JSON.stringify(true)
                );
                this.router.navigate(['/home']).then(() => {
                  window.location.reload();
                });
              },
            });
        },
        error: (err) => console.log(err),
      });
  }
}
