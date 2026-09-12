import {
  ChangeDetectionStrategy,
  Component,
} from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import {
  RouterLink,
  RouterLinkActive,
} from '@angular/router';

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
export class Header {}