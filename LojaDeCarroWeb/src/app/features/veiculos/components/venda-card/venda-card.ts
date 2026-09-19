import {
  ChangeDetectionStrategy,
  Component,
  computed,
  input,
  output,
} from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

import {
  StatusVenda,
  VendaResponse,
} from '../../models/venda-response';

export type ContextoVenda = 'COMPRA' | 'VENDA';

@Component({
  selector: 'app-venda-card',
  imports: [
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './venda-card.html',
  styleUrl: './venda-card.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VendaCard {
  readonly venda = input.required<VendaResponse>();
  readonly contexto = input.required<ContextoVenda>();
  readonly acaoEmAndamento = input(false);

  readonly cancelar = output<number>();
  readonly concluir = output<number>();

  readonly rotulosStatus: Record<StatusVenda, string> = {
    EM_ANDAMENTO: 'Em andamento',
    CONCLUIDA: 'Concluída',
    CANCELADA: 'Cancelada',
  };

  readonly valorFormatado = computed(() =>
    new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL',
    }).format(this.venda().valorVenda)
  );

  readonly dataFormatada = computed(() =>
    new Intl.DateTimeFormat('pt-BR', {
      dateStyle: 'medium',
      timeStyle: 'short',
    }).format(new Date(this.venda().dataVenda))
  );

  readonly participante = computed(() =>
    this.contexto() === 'COMPRA'
      ? this.venda().vendedor
      : this.venda().comprador
  );

  readonly rotuloParticipante = computed(() =>
    this.contexto() === 'COMPRA'
      ? 'Vendido por'
      : 'Comprado por'
  );

  solicitarCancelamento(): void {
    this.cancelar.emit(this.venda().id);
  }

  solicitarConclusao(): void {
    this.concluir.emit(this.venda().id);
  }
}