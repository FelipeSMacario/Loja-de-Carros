import { signal } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { AuthService } from './core/auth/auth-service';
import { App } from './app';

describe('App', () => {
  const falhaInicializacao = signal(false);

  const authServiceMock = {
    falhaInicializacao:
      falhaInicializacao.asReadonly(),
  };

  beforeEach(async () => {
    falhaInicializacao.set(false);

    await TestBed.configureTestingModule({
      imports: [App],
      providers: [
        provideRouter([]),
        {
          provide: AuthService,
          useValue: authServiceMock,
        },
      ],
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(App);
    const app = fixture.componentInstance;

    expect(app).toBeTruthy();
  });

  it('should render the router outlet', () => {
    const fixture = TestBed.createComponent(App);

    fixture.detectChanges();

    const compiled =
      fixture.nativeElement as HTMLElement;

    expect(
      compiled.querySelector('router-outlet')
    ).not.toBeNull();
  });

  it('should not display the authentication warning normally', () => {
    const fixture = TestBed.createComponent(App);

    fixture.detectChanges();

    const element =
      fixture.nativeElement as HTMLElement;

    expect(
      element.querySelector(
        '[data-testid="auth-unavailable"]'
      )
    ).toBeNull();
  });

  it('should display a warning when authentication initialization fails', () => {
    falhaInicializacao.set(true);

    const fixture = TestBed.createComponent(App);
    fixture.detectChanges();

    const element =
      fixture.nativeElement as HTMLElement;

    const aviso = element.querySelector(
      '[data-testid="auth-unavailable"]'
    );

    expect(aviso).not.toBeNull();

    expect(aviso?.textContent)
      .toContain(
        'Autenticação temporariamente indisponível'
      );

    expect(
      element.querySelector(
        '[data-testid="reload-auth-button"]'
      )
    ).not.toBeNull();
  });
});