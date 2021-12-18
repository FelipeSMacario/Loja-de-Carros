import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanActivateChild,
  Router,
  RouterStateSnapshot,
} from '@angular/router';
import { Observable } from 'rxjs';
import { take } from 'rxjs/operators';
import { CarroService } from '../services/carro.service';

@Injectable()
export class ChildGuard implements CanActivateChild {
  constructor(
      private carroService: CarroService,
      private router: Router
      ) {}

  canActivateChild(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> | boolean {


    if (route.params['id']) {
        this.carroService.findCarroById(route.params["id"]).pipe(take(1)).subscribe({
            next : carro => {
                let usuario = JSON.parse(localStorage.getItem("usuario")!);
                if(usuario.id == carro.usuario.id){
                    return true
                }else{
                    this.router.navigate(["/compras"]);
                    return false;
                }
            },
            error : err => console.log(err)
        })
    }      
  

    return true;
    
    
     
  }
}
