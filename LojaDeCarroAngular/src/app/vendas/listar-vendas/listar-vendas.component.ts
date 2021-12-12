import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
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
import { ModalService } from 'src/app/shared/modal/modal.service';
import { ListarKitComponent } from '../listar-kit/listar-kit.component';

@Component({
  selector: 'app-listar-vendas',
  templateUrl: './listar-vendas.component.html',
  styleUrls: ['./listar-vendas.component.css'],
})
export class ListarVendasComponent implements OnInit {
  @ViewChild(ListarKitComponent) child: ListarKitComponent;

  formulario: FormGroup;
  marca: Marca[] = [];
  modelo: Modelo[] = [];
  combustivel: Combustivel[] = [];
  carroceria: Carroceria[] = [];
  cores: Cores[] = [];
  carro: Carro = new Carro();
  id : number;

  constructor(
    private fb: FormBuilder,
    private carroService: CarroService,
    private marcaService: MarcasService,
    private modeloService: ModeloService,
    private combustivelService: CombustivelService,
    private carroceriaService: CarrocerialService,
    private coresService: CoresService,
    private modalService: ModalService,
    private router: Router,
    private activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {

    this.id = this.activatedRoute.snapshot.params["id"];
   if(this.id){
     
     this.criaFormVazio();
 
    this.carroService.findCarroById(this.id).pipe(take(1)).subscribe({
      next : car => this.criaFormPreenchido(car),
      error : err => console.log(err)
    })
     
   }else{
     
     this.criaFormVazio();   
    }   

     this.listarModelos();
    this.listarMarca();    
    this.listarCombustivel();
    this.listarCarroceria();
    this.listarCores();
  }


  criaFormVazio(): void {
    this.formulario = this.fb.group({
      id: [null],
      marca: [null, [Validators.required]],
      modelo: [null, [Validators.required]],
      valor: [null, [Validators.required, Validators.min(5000)]],
      quilometragem: [null, [Validators.required, Validators.min(0), Validators.max(999999)],],
      url: [null],      
      motor: [null, [Validators.required, Validators.maxLength(100)]],
      placa: [null, [Validators.required, Validators.minLength(8), Validators.maxLength(8)],],
      anoFabricacao: [null, [Validators.required, Validators.min(1961), Validators.max(2022)],],
      combustivel: [null, [Validators.required]],
      carroceria: [null, [Validators.required]],
      cores: [null, [Validators.required, Validators.minLength(4), Validators.maxLength(20),],],
      dtCadastro: [new Date()],
      ativo: [true],
      usuario: [JSON.parse(localStorage.getItem('usuario')!)],
    });
  }

  criaFormPreenchido(carro : Carro) {
    this.formulario = this.fb.group({
      id: [carro.id],
      marca: [carro.marca, [Validators.required]],
      modelo: [carro.modelo, [Validators.required]],
      valor: [carro.valor, [Validators.required, Validators.min(5000)]],
      quilometragem: [carro.quilometragem,[Validators.required, Validators.min(0), Validators.max(999999)],],
      url: [carro.url], 
      motor: [carro.motor, [Validators.required, Validators.maxLength(100)]],
      placa: [carro.placa,[Validators.required, Validators.minLength(8), Validators.maxLength(8)],],      
      anoFabricacao: [carro.anoFabricacao, [Validators.required, Validators.min(1961), Validators.max(2022)],],
      combustivel: [carro.combustivel, [Validators.required]],
      carroceria: [carro.carroceria, [Validators.required]],
      cores: [carro.cores,[Validators.required,Validators.minLength(4), Validators.maxLength(20),],],
      dtCadastro: [carro.dtCadastro],
      ativo: [carro.ativo],
      usuario: [carro.usuario],
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

  listarModelos(): void {
    this.modeloService
      .findAllModelos()
      .pipe(take(1))
      .subscribe({
        next: (modelo) =>
         this.modelo = modelo,
        error: (err) => console.log(err),
      });
  }

  filtrarModelo(e : number) : void{
    this.formulario.controls.modelo.setValue(null);
     this.modeloService.findAllModelos().pipe(take(1)).subscribe({
       next : modelo => this.modelo = modelo.filter((model) => model.marca.id === e),
       error : err => console.log(err)
      })
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
          this.modalService.handleMessage(
            'Veículo cadastrado com sucesso',
            'success'
          );
          this.carro = carro;
        },
        error: (err) =>
          this.modalService.handleMessage(
            'Erro ao cadastrar o veículo',
            'danger'
          ),
      });

    const valorAsync = new Promise((resolve, reject) => {
      setTimeout(() => resolve(this.atualizaKit()), 5000);
    }).then(() => this.router.navigate(['/compras']));
  }

  atualizaKit() {
    this.child.cadastrarKit(this.carro);
  }

  abc(){
    
    console.log(this.modelo)
  }
  
}
