import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { take } from 'rxjs/operators';
import { Carro } from 'src/app/models/Carro.model';
import { Imagens } from 'src/app/models/imagens.model';
import { Usuario } from 'src/app/models/usuario.model';
import { CarroService } from 'src/app/services/carro.service';
import { ImagensService } from 'src/app/services/imagens.service';

@Component({
  selector: 'app-carro',
  templateUrl: './carro.component.html',
  styleUrls: ['./carro.component.css'],
})
export class CarroComponent implements OnInit {
  id: number;
  imagens: Imagens[] = [];
  imgdefault: string = '../../../assets/images/semImagem.jpg';
  igual: boolean;
  carro: Carro = new Carro();

  constructor(
    private activatedRoute: ActivatedRoute,
    private imagensService: ImagensService,
    private carroService: CarroService
  ) {}

  ngOnInit(): void {
    this.id = this.activatedRoute.snapshot.params['id'];
    this.listarImagens();
    this.findCarroById(this.id);
  }

  listarImagens() {
    this.imagensService
      .findAllImagens()
      .pipe(take(1))
      .subscribe({
        next: (imagens) => {
          this.imagens = imagens.filter(
            (imagens) => imagens.carro.id == this.id
          );
        },
        error: (err) => console.log(err),
      });
  }

  findCarroById(id: number): void {
    this.carroService
      .findCarroById(this.id)
      .pipe(take(1))
      .subscribe({
        next: (car) => {
          this.carro = car;
          let usuario = JSON.parse(localStorage.getItem("usuario")!);
          car.usuario.id == usuario.id ? this.igual = true : this.igual = false;
        },
        error: (err) => console.log(err),
      });
  }
 
}
