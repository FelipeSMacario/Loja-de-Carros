import { signal } from '@angular/core';
import {
  ComponentFixture,
  TestBed,
} from '@angular/core/testing';
import { vi } from 'vitest';

import {
  AuthService,
  UsuarioAutenticado,
} from '../../../../core/auth/auth-service';
import { ContaDesativada } from './conta-desativada';

describe('ContaDesativada', () => {
  let component: ContaDesativada;
  let fixture: ComponentFixture<ContaDesativada>;

  const usuario = signal<UsuarioAutenticado | null>({
    subject: 'keycloak-user-id',
    nome: 'Jean-Claude Van Damme',
    email: 'van.damme@email.com',
    roles: ['USUARIO'],
  });

  const authServiceMock = {
    usuario,
    sair: vi.fn().mockResolvedValue(undefined),
  };

  beforeEach(async () => {
    authServiceMock.sair.mockClear();

    await TestBed.configureTestingModule({
      imports: [ContaDesativada],
      providers: [
        {
          provide: AuthService,
          useValue: authServiceMock,
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ContaDesativada);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create and display the inactive account', () => {
    const element = fixture.nativeElement as HTMLElement;

    expect(component).toBeTruthy();
    expect(element.textContent)
      .toContain('Sua conta está desativada');
    expect(element.textContent)
      .toContain('van.damme@email.com');
  });

  it('should logout', async () => {
    const button = fixture.nativeElement.querySelector(
      '[data-testid="inactive-account-logout"]'
    ) as HTMLButtonElement;

    button.click();
    await fixture.whenStable();

    expect(authServiceMock.sair)
      .toHaveBeenCalledOnce();
  });
});