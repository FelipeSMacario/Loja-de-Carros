import { Component, Output, EventEmitter, Input, ViewEncapsulation } from '@angular/core';
import { Options } from 'ng5-slider';


@Component({
  selector: 'app-range-simples',
  templateUrl: './range-simples.component.html',
  styleUrls: ['./range-simples.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class RangeSimplesComponent {

  constructor() { }

  @Input() value: number;

  @Output() valorEmitido = new EventEmitter();

  sendValorEmitido(){
    this.valorEmitido.emit(this.value);
  }

  options: Options = {

    getPointerColor: () => {
      return 'black';
    },    
    
    floor: 0,
    ceil: 200000,
    step: 5000
  };

}
