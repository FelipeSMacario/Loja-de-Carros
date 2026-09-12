import {
  ChangeDetectionStrategy,
  Component,
  computed,
  input,
  signal,
} from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { RouterLink } from '@angular/router';
import { environment } from '../../../../../environments/environment';
import { VeiculoResponse } from '../../models/veiculo-response';

@Component({
  selector: 'app-veiculo-card',
  imports: [
    MatCardModule,
  MatIconModule,
  RouterLink,
  ],
  templateUrl: './veiculo-card.html',
  styleUrl: './veiculo-card.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VeiculoCard {
  readonly veiculo = input.required<VeiculoResponse>();

  readonly imagemUrl = computed(() => {
    const imagemId = this.veiculo().imagemPrincipalId;

    return imagemId === null
      ? null
      : `${environment.apiUrl}/imagens/${imagemId}/conteudo`;
  });

  readonly valorFormatado = computed(() =>
    new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL',
    }).format(this.veiculo().valor)
  );

  readonly quilometragemFormatada = computed(() =>
    new Intl.NumberFormat('pt-BR').format(
      this.veiculo().quilometragem
    )
  );

  readonly imagemFalhou = signal(false);

readonly imagemExibidaUrl = computed(() =>
  this.imagemFalhou()
    ? null
    : this.imagemUrl()
);

tratarFalhaImagem(): void {
  this.imagemFalhou.set(true);
}
}