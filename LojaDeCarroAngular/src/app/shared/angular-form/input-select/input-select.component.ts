import { Component, EventEmitter, Input, Output, ViewEncapsulation } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { Objeto } from './objetc.model';

@Component({
  selector: 'app-input-select',
  templateUrl: './input-select.component.html',
  styleUrls: ['./input-select.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class InputSelectComponent {

@Input() label : string;
@Input() formGroup : FormGroup;
@Input() ControlName : string;
@Input() Objeto : Objeto[];
@Output() modelo = new EventEmitter();

atualizaModelo(e){
  this.modelo.emit(e.value.id);
}
}
