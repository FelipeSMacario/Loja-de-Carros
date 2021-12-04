import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { take } from 'rxjs/operators';
import { Carro } from 'src/app/models/Carro.model';
import { Combustivel } from 'src/app/models/combustivel.model';
import { Cores } from 'src/app/models/cores.model';
import { Kit } from 'src/app/models/kit.model';
import { CarroService } from 'src/app/services/carro.service';
import { CombustivelService } from 'src/app/services/combustivel.service';
import { KitService } from 'src/app/services/kit.service';

@Component({
  selector: 'app-detalhes',
  templateUrl: './detalhes.component.html',
  styleUrls: ['./detalhes.component.css']
})
export class DetalhesComponent implements OnInit {

  kit : Kit = new Kit(); 
  combustivel : Combustivel = new Combustivel();
  carro : Carro = new Carro();
  cor : Cores = new Cores();
  id : number;
  placa : string;

  constructor(
    private kitService : KitService,
    private carService : CarroService,
    private combustivelService : CombustivelService,
    private activatedRoute : ActivatedRoute
  ) { }

  ngOnInit(): void {
    this.id = this.activatedRoute.snapshot.params['id'];
    this.findCarroById();
    this.findKitById();
  }

  findKitById() : void {
    this.kitService.findKitById(this.id).pipe(take(1)).subscribe({
      next : (kit) => {this.kit = kit},
      error : err => console.error(err)
      
    })
  }
  
  findCarroById() : void {
    this.carService.findCarroById(this.id).pipe(take(1)).subscribe({
      next : (carro) => {
                        this.carro = carro; 
                        this.cor = carro.cores; 
                        this.placa = carro.placa.substr(carro.placa.length -1);
                        
                        this.combustivelService.findCombustivelById(carro.combustivel.id).pipe(take(1)).subscribe({
                          next : comb => this.combustivel = comb,
                          error : err => console.log(err)
                        })},
      error : err => console.log(err)
    })
  }
 

}

