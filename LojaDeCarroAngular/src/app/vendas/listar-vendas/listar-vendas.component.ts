import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { take } from 'rxjs/operators';
import { Combustivel } from 'src/app/models/combustivel.model';
import { Marca } from 'src/app/models/marca.model';
import { Modelo } from 'src/app/models/modelo.model';
import { CombustivelService } from 'src/app/services/combustivel.service';
import { MarcasService } from 'src/app/services/marcas.service';
import { ModeloService } from 'src/app/services/modelo.service';

@Component({
  selector: 'app-listar-vendas',
  templateUrl: './listar-vendas.component.html',
  styleUrls: ['./listar-vendas.component.css'],
})
export class ListarVendasComponent implements OnInit {
  
  formulario: FormGroup;
  marca : Marca[] = [];
  modelo : Modelo[] = [];
  combustivel : Combustivel[] = [];

  constructor(
              private fb: FormBuilder,
              private marcaService : MarcasService,
              private modeloService : ModeloService,
              private combustivelService : CombustivelService) {}

  ngOnInit(): void {
    this.criaForm();
    this.listarMarca();
    this.listarCombustivel();
  }

  criaForm(): void {
    this.formulario = this.fb.group({
      id: [null],
      marca: [null],
      modelo: [null],
      valor: [null],
      quilometragem: [null],
      url: [null],      
      placa: [null],
      motor: [null],
      anoFabricacao: [null],  
      combustivel: [null],    
      carroceria: [null],      
      cores: [null],        
      usuario: [null],
    });
  }

  listarMarca() : void {
    this.marcaService.findAllMarcas().pipe(take(1)).subscribe({
      next : marca => {
        this.marca = marca
      },
      error : err => console.log(err)
    })
  }

  listarModelos(valor : number) : void {
    this.formulario.controls.modelo.setValue(null);
    this.modeloService.findAllModelos().pipe(take(1)).subscribe({
      next : modelo => this.modelo = modelo.filter(model => model.marca.id === valor),
      error : err => console.log(err)
    })
  }

  listarCombustivel() : void {
    this.combustivelService.listarCombustivel().pipe(take(1)).subscribe({
      next : comb => this.combustivel = comb,
      error : err => console.log(err)
    })
  }
}
