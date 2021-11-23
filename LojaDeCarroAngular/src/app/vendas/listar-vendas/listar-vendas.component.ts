import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { take } from 'rxjs/operators';
import { Carro } from 'src/app/models/Carro.model';
import { Carroceria } from 'src/app/models/carroceria.model';
import { Combustivel } from 'src/app/models/combustivel.model';
import { Cores } from 'src/app/models/cores.model';
import { Marca } from 'src/app/models/marca.model';
import { Modelo } from 'src/app/models/modelo.model';
import { CarroService } from 'src/app/services/carro.service';
import { CarrocerialService } from 'src/app/services/carrocerial.service';
import { CombustivelService } from 'src/app/services/combustivel.service';
import { CoresService } from 'src/app/services/cores.service';
import { MarcasService } from 'src/app/services/marcas.service';
import { ModeloService } from 'src/app/services/modelo.service';
import { ListarKitComponent } from '../listar-kit/listar-kit.component';

@Component({
  selector: 'app-listar-vendas',
  templateUrl: './listar-vendas.component.html',
  styleUrls: ['./listar-vendas.component.css'],  
})

export class ListarVendasComponent implements OnInit {

  @ViewChild(ListarKitComponent ) child: ListarKitComponent; 

  formulario: FormGroup;
  marca: Marca[] = [];
  modelo: Modelo[] = [];
  combustivel: Combustivel[] = [];
  carroceria: Carroceria[] = [];
  cores: Cores[] = [];
  carro : Carro = new Carro();

  

  constructor(
    private fb: FormBuilder,
    private carroService: CarroService,
    private marcaService: MarcasService,
    private modeloService: ModeloService,
    private combustivelService: CombustivelService,
    private carroceriaService: CarrocerialService,
    private coresService: CoresService,

  ) {}

  ngOnInit(): void {
    this.criaForm();
    this.listarMarca();
    this.listarCombustivel();
    this.listarCarroceria();
    this.listarCores();
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

  listarMarca(): void {
    this.marcaService
      .findAllMarcas()
      .pipe(take(1))
      .subscribe({
        next: (marca) => {
          this.marca = marca;
        },
        error: (err) => console.log(err),
      });
  }

  listarModelos(valor: number): void {
    this.formulario.controls.modelo.setValue(null);
    this.modeloService
      .findAllModelos()
      .pipe(take(1))
      .subscribe({
        next: (modelo) =>
          (this.modelo = modelo.filter((model) => model.marca.id === valor)),
        error: (err) => console.log(err),
      });
  }

  listarCombustivel(): void {
    this.combustivelService
      .listarCombustivel()
      .pipe(take(1))
      .subscribe({
        next: (comb) => (this.combustivel = comb),
        error: (err) => console.log(err),
      });
  }

  listarCarroceria(): void {
    this.carroceriaService
      .findAllCarroceria()
      .pipe(take(1))
      .subscribe({
        next: (carroceria) => (this.carroceria = carroceria),
        error: (err) => console.log(err),
      });
  }

  listarCores(): void {
    this.coresService
      .findAllCores()
      .pipe(take(1))
      .subscribe({
        next: (cor) => (this.cores = cor),
        error: (err) => console.log(err),
      });
  }  

  cadastrarCarro() {
    this.carroService
      .cadastrarCarro(this.formulario.value)
      .pipe(take(1))
      .subscribe({
        next: (carro) => {
          console.log('Cadastrado com sucesso principal', carro);
          this.carro = carro;                         
        },
        error: (err) => console.log(err),
      }); 

      const valorAsync = new Promise((resolve, reject) => {
        setTimeout(() => resolve(this.atualizaKit()), 5000)
      });
  }

  atualizaKit(){
    this.child.cadastrarKit(this.carro);
  }
}
