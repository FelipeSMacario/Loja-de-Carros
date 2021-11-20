import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { take } from 'rxjs/operators';
import { Carro } from 'src/app/models/Carro.model';
import { Imagens } from 'src/app/models/imagens.model';
import { CarroService } from 'src/app/services/carro.service';
import { ImagensService } from 'src/app/services/imagens.service';


@Component({
  selector: 'app-carro',
  templateUrl: './carro.component.html',
  styleUrls: ['./carro.component.css']
})
export class CarroComponent implements OnInit {

  id : number;
  imagens : Imagens[] = [];

  constructor(
    
    private activatedRoute : ActivatedRoute,
    private imagensService : ImagensService
  ) { }

  ngOnInit(): void {
    this.id = this.activatedRoute.snapshot.params['id'];
    
    this.listarImagens();
  }

  listarImagens(){
    this.imagensService.findAllImagens().subscribe({
      next : imagens => {
        this.imagens = imagens.filter(imagens => imagens.carro.id == this.id);
        console.log(this.imagens)
        },
      error : err => console.log(err)
    } )    
  }


}
