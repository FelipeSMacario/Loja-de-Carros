import { state } from '@angular/animations';
import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanActivate,
  Router,
  RouterStateSnapshot,
  UrlTree,
} from '@angular/router';
import { Observable } from 'rxjs';
import { take } from 'rxjs/operators';
import { CarroService } from '../services/carro.service';
import { ListarVendasComponent } from '../vendas/listar-vendas/listar-vendas.component';

@Injectable()
export class AuthGuard implements CanActivate {
  constructor(private router: Router, private carroService: CarroService) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> | boolean {
    if (localStorage.getItem('usuario')) {
      return true;
    }

    this.router.navigate(['/login']);

    return false;
  }
}
