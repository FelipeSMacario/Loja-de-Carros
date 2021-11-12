import { Component, Input, Output, ViewEncapsulation, EventEmitter } from '@angular/core';
import { PageChangedEvent } from 'ngx-bootstrap/pagination';



@Component({
  selector: 'app-paginacao',
  templateUrl: './paginacao.component.html',
  styleUrls: ['./paginacao.component.css'],
  encapsulation: ViewEncapsulation.None
  
})
export class PaginacaoComponent{
  currentPage = 1;
  page? : number;
  @Output() atualizaPagina = new EventEmitter();
  @Input() itemsPerPage : number;
  @Input() totalItems : number;
 
  pageChanged(event: PageChangedEvent): void {
    this.page = event.page;
    this.atualizaPagina?.emit(this.page);
  }
}
