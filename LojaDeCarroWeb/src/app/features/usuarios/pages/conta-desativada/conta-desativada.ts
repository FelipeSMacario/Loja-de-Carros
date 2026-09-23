import {
  ChangeDetectionStrategy,
  Component,
  inject,
} from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

import {
  AuthService,
} from '../../../../core/auth/auth-service';

@Component({
  selector: 'app-conta-desativada',
  imports: [
    MatButtonModule,
    MatIconModule,
  ],
  templateUrl: './conta-desativada.html',
  styleUrl: './conta-desativada.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ContaDesativada {
  readonly auth = inject(AuthService);

  async sair(): Promise<void> {
    await this.auth.sair();
  }
}