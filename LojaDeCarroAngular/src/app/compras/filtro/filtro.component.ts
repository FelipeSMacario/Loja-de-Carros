import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Marca } from 'src/app/models/marca.model';
import { Modelo } from 'src/app/models/modelo.model';
import { MarcasService } from 'src/app/services/marcas.service';
import { ModeloService } from 'src/app/services/modelo.service';

@Component({
  selector: 'app-filtro',
  templateUrl: './filtro.component.html',
  styleUrls: ['./filtro.component.css']
})
export class FiltroComponent implements OnInit {

  marca : Marca[] = [];
  modelo : Modelo[] = [];
  filtro : FormGroup;

  constructor(
    private fb : FormBuilder,
    private marcaService : MarcasService,
    private modeloService : ModeloService) { }

  ngOnInit(): void {
    this.listarMarcas();

    this.filtro = this.fb.group({
      id: [null],
      marca: [null],
      modelo: [null],
      anoFabricacao: [null],
      valor: [null],
      quilometragem: [null],
      
    })
  }

  listarMarcas() : void {
      this.marcaService.findAllMarcas().subscribe({
      next : (marca) => (this.marca = marca),
      error : (err) => console.log(err)
    })
  }

  

listarModelos(valor : number){
  this.filtro.controls.modelo.setValue(null);
  console.log(this.filtro)
  this.modeloService.findAllModelos().subscribe({
    next : (modelo) => {this.modelo = modelo.filter( modelo => modelo.marca.id === valor)},
    error : (err) => console.log(err)
  })
  
}

}
