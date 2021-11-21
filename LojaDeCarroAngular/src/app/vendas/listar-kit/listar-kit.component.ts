import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';

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

  constructor(private kitService: KitService, private fb: FormBuilder) {}

  ngOnInit(): void {
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

  digitaCarro(carro: Object) {
    console.log(carro);
  }
}
