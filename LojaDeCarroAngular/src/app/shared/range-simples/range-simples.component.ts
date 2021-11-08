import { Component, Output, EventEmitter, Input } from '@angular/core';
import { Options } from 'ng5-slider';


@Component({
  selector: 'app-range-simples',
  templateUrl: './range-simples.component.html',
  styleUrls: ['./range-simples.component.css']
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
    ceil: 1000000
  };

}
