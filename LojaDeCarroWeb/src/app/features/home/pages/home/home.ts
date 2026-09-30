import {
  ChangeDetectionStrategy,
  Component,
  DestroyRef,
  OnInit,
  inject,
  signal,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MatButtonModule } from '@angular/material/button';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { finalize } from 'rxjs';

import { VeiculoCard } from '../../../veiculos/components/veiculo-card/veiculo-card';
import { VeiculoFiltros } from '../../../veiculos/components/veiculo-filtros/veiculo-filtros';
import { VeiculoApi } from '../../../veiculos/data-access/veiculo-api';
import { VeiculoFiltro } from '../../../veiculos/models/veiculo-filtro';
import { VeiculoResponse } from '../../../veiculos/models/veiculo-response';

@Component({
  selector: 'app-home',
  imports: [
    MatButtonModule,
    MatPaginatorModule,
    MatProgressSpinnerModule,
    VeiculoCard,
    VeiculoFiltros,
  ],
  templateUrl: './home.html',
  styleUrl: './home.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Home implements OnInit {
  private readonly veiculoApi = inject(VeiculoApi);
  private readonly destroyRef = inject(DestroyRef);

  readonly totalElementos = signal(0);
  readonly paginaAtual = signal(0);
  readonly tamanhoPagina = signal(9);

  readonly veiculos = signal<VeiculoResponse[]>([]);
  readonly carregando = signal(true);
  readonly erro = signal(false);

  readonly filtroAtual = signal<VeiculoFiltro>({});

  ngOnInit(): void {
    this.carregarVeiculos();
  }

  carregarVeiculos(pagina = 0, tamanho = this.tamanhoPagina()): void {
    this.carregando.set(true);
    this.erro.set(false);

    this.veiculoApi
      .listarAtivos(pagina, tamanho, this.filtroAtual())
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        finalize(() => this.carregando.set(false)),
      )
      .subscribe({
        next: (response) => {
          this.veiculos.set(response.content);
          this.totalElementos.set(response.totalElements);
          this.paginaAtual.set(response.number);
          this.tamanhoPagina.set(response.size);
        },
        error: () => {
          this.veiculos.set([]);
          this.totalElementos.set(0);
          this.erro.set(true);
        },
      });
  }

  aplicarFiltros(filtro: VeiculoFiltro): void {
    this.filtroAtual.set(filtro);
    this.paginaAtual.set(0);

    this.carregarVeiculos(0, this.tamanhoPagina());
  }

  alterarPagina(evento: PageEvent): void {
    this.carregarVeiculos(evento.pageIndex, evento.pageSize);
  }
}
