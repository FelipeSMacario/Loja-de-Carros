import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Carro } from '../models/Carro.model';
import { CarroService } from '../services/carro.service';

@Component({
  selector: 'app-carro',
  templateUrl: './carro.component.html',
  styleUrls: ['./carro.component.css']
})
export class CarroComponent implements OnInit {

  id : number;
  carro : Carro;

  constructor(
    private carroService : CarroService,
    private activatedRoute : ActivatedRoute
  ) { }

  ngOnInit(): void {
    this.id = this.activatedRoute.snapshot.params['id'];
    this.carroService.findCarroById(this.id).subscribe({
      next : (carro) => this.carro = carro,
      error : err => console.log(err)
    } )
    
  }

}
