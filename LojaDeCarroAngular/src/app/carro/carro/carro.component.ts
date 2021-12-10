import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { EMPTY } from 'rxjs';
import { switchMap, take } from 'rxjs/operators';
import { Carro } from 'src/app/models/Carro.model';
import { Imagens } from 'src/app/models/imagens.model';
import { CarroService } from 'src/app/services/carro.service';
import { ImagensService } from 'src/app/services/imagens.service';
import { ModalService } from 'src/app/shared/modal/modal.service';

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
  carroMarca: string;
  carroModelo: string;

  constructor(
    private activatedRoute: ActivatedRoute,
    private imagensService: ImagensService,
    private carroService: CarroService,
    private modalService: ModalService,
    private router : Router
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
          this.carroMarca = car.marca.nome;
          this.carroModelo = car.modelo.nome;
          let usuario = JSON.parse(localStorage.getItem('usuario')!);
          car.usuario.id == usuario.id
            ? (this.igual = true)
            : (this.igual = false);
        },
        error: (err) => console.log(err),
      });
  }

  marcaVendido() {
    const result$ = this.modalService.showConfirm(
      'Confirmar como vendido',
      'Deseja marcar o veículo como vendido? O veículo não será mais apresentado na lista de vendas',
      'Confirmar',
      'Cancelar',
      'danger'
    );
    result$
      .asObservable()
      .pipe(
        take(1),
        switchMap((result) =>
          result ? this.carroService.marcaCarroVendido(this.carro) : EMPTY
        )
      )
      .subscribe(
        (sucess) => {
          this.modalService.handleMessage(
            'Carro marcado como vendido',
            'success'
          );
        },
        (error) => {
          this.modalService.handleMessage(
            'Erro ao atualizar o carro',
            'danger'
          );
          console.log('Erro', error);
        }
      );
  }

  editarCarro(){
    this.router.navigate(["/vendas/", this.id])
  }
  comprar(){
    this.router.navigate(["comprar/", this.id])
  }
}
