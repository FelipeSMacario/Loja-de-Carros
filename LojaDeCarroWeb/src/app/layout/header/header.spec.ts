import { ComponentFixture, TestBed, } from '@angular/core/testing';
import { signal } from '@angular/core';
import { provideRouter } from '@angular/router';
import { vi } from 'vitest';
import { OverlayContainer } from '@angular/cdk/overlay';
import { AuthService, UsuarioAutenticado, } from '../../core/auth/auth-service';
import { Header } from './header';

describe('Header', () => {
  let component: Header;
  let fixture: ComponentFixture<Header>;
  let overlayContainer: OverlayContainer;

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

    overlayContainer = TestBed.inject(OverlayContainer);
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

    expect(
      element.querySelector(
        '[data-testid="my-purchases-link"]'
      )
    ).toBeNull();

    expect(
      element.querySelector(
        '[data-testid="my-sales-link"]'
      )
    ).toBeNull();

    button.click();
  });

  it('should display authenticated navigation in the mobile menu', async () => {
    autenticado.set(true);
    usuario.set({
      subject: 'keycloak-user-id',
      nome: 'Steven Seagal',
      email: 'steven@email.com',
      roles: ['USUARIO'],
    });

    fixture.detectChanges();

    const element = fixture.nativeElement as HTMLElement;

    const menuButton = element.querySelector(
      '[data-testid="mobile-menu-button"]'
    ) as HTMLButtonElement;

    expect(menuButton).not.toBeNull();

    menuButton.click();

    await fixture.whenStable();
    fixture.detectChanges();

    const overlay =
      overlayContainer.getContainerElement();

    expect(overlay.textContent)
      .toContain('Steven Seagal');

    expect(
      overlay.querySelector(
        '[data-testid="mobile-stock-link"]'
      )?.getAttribute('href')
    ).toBe('/home');

    expect(
      overlay.querySelector(
        '[data-testid="mobile-announce-link"]'
      )?.getAttribute('href')
    ).toBe('/veiculos/anunciar');

    expect(
      overlay.querySelector(
        '[data-testid="mobile-my-ads-link"]'
      )?.getAttribute('href')
    ).toBe('/veiculos/meus-anuncios');

    expect(
      overlay.querySelector(
        '[data-testid="mobile-my-purchases-link"]'
      )?.getAttribute('href')
    ).toBe('/vendas/minhas-compras');

    expect(
      overlay.querySelector(
        '[data-testid="mobile-my-sales-link"]'
      )?.getAttribute('href')
    ).toBe('/vendas/minhas-vendas');

    const logoutButton = overlay.querySelector(
      '[data-testid="mobile-logout-button"]'
    ) as HTMLButtonElement;

    logoutButton.click();

    expect(authServiceMock.sair)
      .toHaveBeenCalledOnce();
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

    const myAdsLink = element.querySelector(
      '[data-testid="my-ads-link"]'
    );

    const myPurchasesLink = element.querySelector(
      '[data-testid="my-purchases-link"]'
    );

    const mySalesLink = element.querySelector(
      '[data-testid="my-sales-link"]'
    );

    expect(myAdsLink?.getAttribute('href'))
      .toBe('/veiculos/meus-anuncios');

    expect(myPurchasesLink?.getAttribute('href'))
      .toBe('/vendas/minhas-compras');

    expect(mySalesLink?.getAttribute('href'))
      .toBe('/vendas/minhas-vendas');

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