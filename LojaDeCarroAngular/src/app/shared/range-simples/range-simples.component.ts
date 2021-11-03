import { Component } from '@angular/core';
import { Options } from 'ng5-slider';

@Component({
  selector: 'app-range-simples',
  templateUrl: './range-simples.component.html',
  styleUrls: ['./range-simples.component.css']
})
export class RangeSimplesComponent {

  constructor() { }

  value: number = 0;
  options: Options = {
    getPointerColor: ()=>{return "black"}, 
    
    floor: 0,
    ceil: 1000000
  };

}
