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
  checkMarca: string;
  checkModelo: string;

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
      anoInicio: [1961],
      anoFim: [2022],
      valorInicio: [0],
      valorFim: [200000],
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
    this.filtro.value.valorFim = parseFloat(valor[1]);
  }

  pesquisa() {
    if (this.filtro.value.marca) this.checkMarca = this.filtro.value.marca.nome;
    if (this.filtro.value.modelo)
      this.checkModelo = this.filtro.value.modelo.nome;

    this.router.navigate(['/compras/search'], {
      queryParams: {
        marca: this.checkMarca,
        modelo: this.checkModelo,
        anoInicio: this.filtro.value.anoInicio,
        anoFim: this.filtro.value.anoFim,
        valorInicio: this.filtro.value.valorInicio,
        valorFim: this.filtro.value.valorFim,
        quilometragem: this.filtro.value.quilometragem,
        page : 1
      },
    });
  }

  limparFiltro() {
    this.filtro.reset();
    this.pesquisa();
    this.router.navigate(['/compras']);
  }
}
