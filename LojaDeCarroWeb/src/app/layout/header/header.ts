import {
  ChangeDetectionStrategy,
  Component,
  inject,
} from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import {
  RouterLink,
  RouterLinkActive,
} from '@angular/router';

import { AuthService } from '../../core/auth/auth-service';

@Component({
  selector: 'app-header',
  imports: [
    MatIconModule,
    RouterLink,
    RouterLinkActive,
  ],
  templateUrl: './header.html',
  styleUrl: './header.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Header {
  readonly auth = inject(AuthService);

  async entrar(): Promise<void> {
    await this.auth.entrar();
  }

  async sair(): Promise<void> {
    await this.auth.sair();
  }
}