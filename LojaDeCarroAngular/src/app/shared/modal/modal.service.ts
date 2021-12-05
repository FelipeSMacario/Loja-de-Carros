import { Injectable } from '@angular/core';
import { BsModalRef, BsModalService } from 'ngx-bootstrap/modal';
import { AlertModalComponent } from './alert-modal/alert-modal.component';
import { ModalComponent } from './modal.component';

@Injectable({
  providedIn: 'root'
})
export class ModalService {

  constructor(
    private modalService : BsModalService,
  ) { }

  handleMessage(message : string, type? : string){
    const bsModalRef : BsModalRef = this.modalService.show(AlertModalComponent);
    bsModalRef.content.message = message;

    if(type) bsModalRef.content.type = type;
  }

  showConfirm(title : string, msg : string, txtOk? : string, txtCancel? : string, type? : string){
    const bsModalRef : BsModalRef = this.modalService.show(ModalComponent);

    bsModalRef.content.title = title;
    bsModalRef.content.msg = msg;

    if(txtOk)
    bsModalRef.content.txtOk = txtOk;

    if(txtCancel)
    bsModalRef.content.txtCancel = txtCancel;

    if(type)
    bsModalRef.content.type = type;

    return (<ModalComponent>bsModalRef.content).confirmResult;
  }
  }
