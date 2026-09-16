import {
  ComponentFixture,
  TestBed,
} from '@angular/core/testing';
import { signal } from '@angular/core';
import { provideRouter } from '@angular/router';
import { vi } from 'vitest';

import {
  AuthService,
  UsuarioAutenticado,
} from '../../core/auth/auth-service';
import { Header } from './header';

describe('Header', () => {
  let component: Header;
  let fixture: ComponentFixture<Header>;

  const inicializado = signal(true);
  const autenticado = signal(false);
  const usuario = signal<UsuarioAutenticado | null>(null);

  const authServiceMock = {
    inicializado,
    autenticado,
    usuario,
    entrar: vi.fn().mockResolvedValue(undefined),
    sair: vi.fn().mockResolvedValue(undefined),
  };

  beforeEach(async () => {
    inicializado.set(true);
    autenticado.set(false);
    usuario.set(null);

    authServiceMock.entrar.mockClear();
    authServiceMock.sair.mockClear();

    await TestBed.configureTestingModule({
      imports: [Header],
      providers: [
        provideRouter([]),
        {
          provide: AuthService,
          useValue: authServiceMock,
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(Header);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display login for a visitor', () => {
    const element = fixture.nativeElement as HTMLElement;
    const button = element.querySelector(
      '[data-testid="login-button"]'
    ) as HTMLButtonElement;

    expect(button).not.toBeNull();

    button.click();

    expect(authServiceMock.entrar).toHaveBeenCalledOnce();
  });

  it('should display the user and logout when authenticated', () => {
    autenticado.set(true);
    usuario.set({
      subject: 'keycloak-user-id',
      nome: 'Felipe',
      email: 'felipe@email.com',
      roles: ['VENDEDOR'],
    });

    fixture.detectChanges();

    const element = fixture.nativeElement as HTMLElement;
    const user = element.querySelector(
      '[data-testid="authenticated-user"]'
    );
    const button = element.querySelector(
      '[data-testid="logout-button"]'
    ) as HTMLButtonElement;

    const announceLink = element.querySelector(
      '[data-testid="announce-link"]'
    );

    expect(announceLink?.getAttribute('href'))
      .toBe('/veiculos/anunciar');

    expect(user?.textContent).toContain('Felipe');
    expect(button).not.toBeNull();
    expect(
      element.querySelector('[data-testid="login-button"]')
    ).toBeNull();

    button.click();

    expect(authServiceMock.sair).toHaveBeenCalledOnce();
  });
});