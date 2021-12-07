import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { take } from 'rxjs/operators';
import { Kit } from 'src/app/models/kit.model';
import { KitService } from 'src/app/services/kit.service';

@Component({
  selector: 'app-listar-kit',
  templateUrl: './listar-kit.component.html',
  styleUrls: ['./listar-kit.component.css'],
})
export class ListarKitComponent implements OnInit {
  kit: Kit = new Kit();
  formCheck: FormGroup;
  id : number;

  constructor(
    private kitService: KitService, 
    private fb: FormBuilder,
    private activatedRoute : ActivatedRoute
    ) {}

  ngOnInit(): void {
    this.id = this.activatedRoute.snapshot.params["id"];

    if(this.id){
      this.formVazio();
      this.kitService.findKitById(this.id).pipe(take(1)).subscribe({
        next : ki => this.formPreenchido(ki),
        error : err => console.log(err)
      })

    } 
    else {
      this.formVazio();
    }
  }

  formVazio(){
    this.formCheck = this.fb.group({
      id: [null],
      arCondicionado: [false],
      automatico: [false],
      direcaoHidraulica: [false],
      freioABS: [false],
      rodaLigaLeve: [false],
      carro: [null],
      bancoCouro: [false],
      quatroPortas: [false],
    });
  }
  formPreenchido(kit : Kit){
    this.formCheck = this.fb.group({
      id: [kit.id],
      arCondicionado: [kit.arCondicionado],
      automatico: [kit.automatico],
      direcaoHidraulica: [kit.direcaoHidraulica],
      freioABS: [kit.freioABS],
      rodaLigaLeve: [kit.rodaLigaLeve],
      carro: [kit.carro],
      bancoCouro: [kit.bancoCouro],
      quatroPortas: [kit.quatroPortas],
    });
  }

  cadastrarKit(carro : Object): void {
    this.formCheck.controls.carro.setValue(carro);
    this.kitService
      .saveKit(this.formCheck.value)
      .pipe(take(1))
      .subscribe({
        next: (kit) => console.log('SAlvo kit', kit),
        error: (err) => console.log(err),
      });
  }

}
