import { Component, Input } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-e-mail',
  templateUrl: './e-mail.component.html',
  styleUrls: ['./e-mail.component.css']
})
export class EMailComponent {

  @Input() formGroup : FormGroup
 
  @Input() email : string;

}
