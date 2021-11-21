import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Carro } from 'src/app/models/Carro.model';
import { Cores } from 'src/app/models/cores.model';
import { Kit } from 'src/app/models/kit.model';
import { CarroService } from 'src/app/services/carro.service';
import { KitService } from 'src/app/services/kit.service';

@Component({
  selector: 'app-detalhes',
  templateUrl: './detalhes.component.html',
  styleUrls: ['./detalhes.component.css']
})
export class DetalhesComponent implements OnInit {

  kits : Kit[] = [];
  kit : Kit = new Kit(); 
  carro : Carro = new Carro();
  cor : Cores = new Cores();
  id : number;
  placa : string;

  constructor(
    private kitService : KitService,
    private carService : CarroService,
    private activatedRoute : ActivatedRoute
  ) { }

  ngOnInit(): void {
    this.id = this.activatedRoute.snapshot.params['id'];
    this.findCarroById();
    this.findKitById();
  }

  findKitById() : void {
    this.kitService.findKitById(this.id).subscribe({
      next : (kit) => {this.kit = kit},
      error : err => console.error(err)
      
    })
  }
  
  findCarroById() : void {
    this.carService.findCarroById(this.id).subscribe({
      next : (carro) => {this.carro = carro; this.cor = carro.cores; this.placa = carro.placa.substr(carro.placa.length -1)},
      error : err => console.log(err)
    })
  }

  abc(){
    console.log(this.kit);
  }

}

