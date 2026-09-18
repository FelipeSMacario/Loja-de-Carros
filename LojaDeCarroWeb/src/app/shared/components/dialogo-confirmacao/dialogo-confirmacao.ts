import {
  ChangeDetectionStrategy,
  Component,
  inject,
} from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import {
  MAT_DIALOG_DATA,
  MatDialogModule,
  MatDialogRef,
} from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';

export interface DialogoConfirmacaoData {
  titulo: string;
  mensagem: string;
  textoConfirmar?: string;
  textoCancelar?: string;
  tipo?: 'aviso' | 'perigo';
}

@Component({
  selector: 'app-dialogo-confirmacao',
  imports: [
    MatButtonModule,
    MatDialogModule,
    MatIconModule,
  ],
  templateUrl: './dialogo-confirmacao.html',
  styleUrl: './dialogo-confirmacao.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DialogoConfirmacao {
  readonly data = inject<DialogoConfirmacaoData>(
    MAT_DIALOG_DATA
  );

  private readonly dialogRef = inject(
    MatDialogRef<DialogoConfirmacao, boolean>
  );

  readonly tipo = this.data.tipo ?? 'aviso';

  cancelar(): void {
    this.dialogRef.close(false);
  }

  confirmar(): void {
    this.dialogRef.close(true);
  }
}