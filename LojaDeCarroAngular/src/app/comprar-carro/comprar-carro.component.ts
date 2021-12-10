import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { EMPTY } from 'rxjs';
import { switchMap, take } from 'rxjs/operators';
import { Carro } from '../models/Carro.model';
import { CarroService } from '../services/carro.service';
import { ComprarService } from '../services/comprar.service';
import { ModalService } from '../shared/modal/modal.service';

@Component({
  selector: 'app-comprar-carro',
  templateUrl: './comprar-carro.component.html',
  styleUrls: ['./comprar-carro.component.css']
})
export class ComprarCarroComponent implements OnInit {

  id : number;
  formulario : FormGroup;
  carroMarca : string;
  carroModelo : string;
  valor : number;


  constructor(
    private activatedRoute : ActivatedRoute,
    private fb : FormBuilder,
    private carroService : CarroService,
    private comprasService : ComprarService,
    private modalService : ModalService
    ) { }

  ngOnInit(): void {
    this.id = this.activatedRoute.snapshot.params["id"];
    this.formVazio();
    this.carroService.findCarroById(this.id).pipe(take(1)).subscribe({
      next : car => {
        this.criarForm(car);
        this.carroMarca = car.marca.nome;
        this.carroModelo = car.modelo.nome;
        this.valor = car.valor
      }
    })
  }

  formVazio(){
    this.formulario = this.fb.group({
      id : [null],
      comprador : [null],
      vendedor : [null],
      carro : [null],
      valor : [null],
      dataVenda : [null]
  })
}

  criarForm(carro : Carro){
    this.formulario = this.fb.group({
      id : [null],
      comprador : [JSON.parse(localStorage.getItem("usuario")!)],
      vendedor : [carro.usuario],
      carro : [carro],
      valor : [carro.valor],
      dataVenda : [new Date()]
    })
  }

  modalComprar(){
    const result$ = this.modalService.showConfirm("Confirma compra", "Deseja comprar o veículo?", "Confirmar", "Cancelar" );
    result$.asObservable().pipe(
      take(1),
      switchMap(result => result ? this.comprasService.comprar(this.formulario.value) : EMPTY)
    ).subscribe(
      sucess => {this.modalService.handleMessage("Veículo adquirido", "success")},
      error => this.modalService.handleMessage("Erro ao adquirir o veículo", "danger")
    )
  }

  comprar(){
    this.comprasService.comprar(this.formulario.value).pipe(take(1)).subscribe({
      next : buy => console.log("Compra Realizada"),
      error : buy => console.log("Falha")
    })
  }
}
