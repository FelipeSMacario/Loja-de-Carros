import { Component, Input } from '@angular/core';
import { FormGroup } from '@angular/forms';

@Component({
  selector: 'app-input-number',
  templateUrl: './input-number.component.html',
  styleUrls: ['./input-number.component.css']
})
export class InputNumberComponent {

@Input() formGroup : FormGroup;
@Input() ControlName : string;  
@Input() label : string;
@Input() placeholder : string;
@Input() min : string;
@Input() step : string;
@Input() prefixo : string;

}
