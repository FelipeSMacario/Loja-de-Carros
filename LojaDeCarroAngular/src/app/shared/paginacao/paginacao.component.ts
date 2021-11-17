import { Component, Input, Output, ViewEncapsulation, EventEmitter } from '@angular/core';
import { Router } from '@angular/router';
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

  constructor(private router : Router){}
 
  pageChanged(event: PageChangedEvent): void {
    this.page = event.page;
    this.atualizaPagina?.emit(this.page);
    
    if(this.router.url.includes("search")) this.router.navigate(["/compras/search"], {queryParams : {"page" : this.page}, queryParamsHandling : "merge"});
    else this.router.navigate(["/compras"], {queryParams : {"page" : this.page}, queryParamsHandling : "merge"});


    
    
  }
}
