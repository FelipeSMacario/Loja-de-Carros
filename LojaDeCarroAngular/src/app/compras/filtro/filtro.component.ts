import { HttpParams } from '@angular/common/http';
import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';

import { Marca } from 'src/app/models/marca.model';
import { Modelo } from 'src/app/models/modelo.model';
import { MarcasService } from 'src/app/services/marcas.service';
import { ModeloService } from 'src/app/services/modelo.service';

@Component({
  selector: 'app-filtro',
  templateUrl: './filtro.component.html',
  styleUrls: ['./filtro.component.css'],
})
export class FiltroComponent implements OnInit {
  marca: Marca[] = [];
  modelo: Modelo[] = [];
  filtro: FormGroup;

  @Output() parametros = new EventEmitter();

  constructor(
    private fb: FormBuilder,
    private marcaService: MarcasService,
    private modeloService: ModeloService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.listarMarcas();

    this.filtro = this.fb.group({
      marca: [null],
      modelo: [null],
      anoInicio: [null],
      anoFim: [null],
      valorInicio: [null],
      valorFinal: [null],
      quilometragem: [null],
    });
  }

  listarMarcas(): void {
    this.marcaService.findAllMarcas().subscribe({
      next: (marca) => (this.marca = marca),
      error: (err) => console.log(err),
    });
  }

  listarModelos(valor: number) {
    this.filtro.controls.modelo.setValue(null);
    this.modeloService.findAllModelos().subscribe({
      next: (modelo) => {
        this.modelo = modelo.filter((modelo) => modelo.marca.id === valor);
      },
      error: (err) => console.log(err),
    });
  }

  atualizaQuilometragem(e) {
    this.filtro.value.quilometragem = e;
  }

  atualizaAno(e) {
    const valor = e;
    this.filtro.value.anoInicio = parseInt(valor[0]);
    this.filtro.value.anoFim = parseInt(valor[1]);
  }

  atualizaValor(e) {
    const valor = e;
    this.filtro.value.valorInicio = parseFloat(valor[0]);
    this.filtro.value.valorFinal = parseFloat(valor[1]);
  }

  pesquisa() {
    let httpParams = new HttpParams();

    if (this.filtro.value.marca) httpParams = httpParams.set("marca",this.filtro.value.marca.nome);
    if (this.filtro.value.modelo) httpParams = httpParams.set("modelo",this.filtro.value.modelo.nome);
    if (this.filtro.value.anoFabricacaoInicio) httpParams = httpParams.set("anoFabricacaoInicio",this.filtro.value.anoFabricacaoInicio);
    if (this.filtro.value.anoFabricacaoFinal) httpParams = httpParams.set("anoFabricacaoFinal",this.filtro.value.anoFabricacaoFinal);
    if (this.filtro.value.valorInicio) httpParams = httpParams.set("valorInicio",this.filtro.value.valorInicio);
    if (this.filtro.value.valorFinal) httpParams = httpParams.set("valorFinal",this.filtro.value.valorFinal);
    if (this.filtro.value.quilometragem) httpParams = httpParams.set("quilometragem",this.filtro.value.quilometragem);
    
    this.router.navigate(["/compras"], {queryParams : {"search" : httpParams}})
    this.parametros.emit( httpParams.toString());
    
  }

  limparFiltro() {
    this.filtro.reset();
    this.pesquisa();
    this.router.navigate(['/compras']);
  }

  refresh(): void {
    
  }

}
