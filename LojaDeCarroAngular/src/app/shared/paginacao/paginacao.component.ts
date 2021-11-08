import { Component, Input, ViewEncapsulation } from '@angular/core';
import { PageChangedEvent } from 'ngx-bootstrap/pagination';


@Component({
  selector: 'app-paginacao',
  templateUrl: './paginacao.component.html',
  styleUrls: ['./paginacao.component.css'],
  encapsulation: ViewEncapsulation.None
  
})
export class PaginacaoComponent{
  @Input() totalItems : number;
  @Input() itemsPerPage : number;
  currentPage = 1;
  smallnumPages = 0;
 
  pageChanged(event: PageChangedEvent): void {
    console.log('Page changed to: ' + event.page);
    console.log('Number items per page: ' + event.itemsPerPage);
  }
}
