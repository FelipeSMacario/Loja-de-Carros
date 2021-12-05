import { Component, Input, OnInit } from '@angular/core';
import { Subject } from 'rxjs';
import { BsModalRef} from 'ngx-bootstrap/modal';

@Component({
  selector: 'app-modal',
  templateUrl: './modal.component.html',
  styleUrls: ['./modal.component.css']
})
export class ModalComponent implements OnInit {

  @Input() title : string;
  @Input() msg : string;
  @Input() txtOk : string = "Confirmar";
  @Input() txtCancel : string = "Cancelar";
  @Input() type : string = "primary";

  constructor(
    private bsModalRef : BsModalRef
  ) { }

  ngOnInit(): void {
    this.confirmResult = new Subject();
  }

  confirmResult : Subject<boolean>;

  onConfirm(){
    this.confirmAndClose(true);
  }

  onClose(){
    this.confirmAndClose(false);
  }

  private confirmAndClose(value : boolean){
    this.confirmResult.next(value);
    this.bsModalRef.hide();
  }

}
