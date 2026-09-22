import { ComponentFixture, TestBed, } from '@angular/core/testing';
import { signal } from '@angular/core';
import { provideRouter } from '@angular/router';
import { vi } from 'vitest';
import { OverlayContainer } from '@angular/cdk/overlay';
import { AuthService, UsuarioAutenticado, } from '../../core/auth/auth-service';
import { Header } from './header';
import { of } from 'rxjs';

import {
  UsuarioApi,
  UsuarioAtualResponse,
} from '../../features/usuarios/data-access/usuario-api';

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
    possuiRole: vi.fn(),
  };

  const perfilLocal =
    signal<UsuarioAtualResponse | null>(null);

  const usuarioLocal: UsuarioAtualResponse = {
    id: 3,
    nome: 'Felipe',
    cpf: '12345678901',
    dataNascimento: '1991-05-14',
    email: 'felipe@email.com',
    ativo: true,
  };

  const usuarioApiMock = {
    usuarioAtual: perfilLocal.asReadonly(),
    buscarAtual: vi.fn(),
    limparUsuarioAtual: vi.fn(() => {
      perfilLocal.set(null);
    }),
  };

  beforeEach(async () => {
    perfilLocal.set(null);
    authServiceMock.possuiRole
      .mockReset()
      .mockReturnValue(false);

    usuarioApiMock.buscarAtual
      .mockReset()
      .mockImplementation(() => {
        perfilLocal.set(usuarioLocal);
        return of(usuarioLocal);
      });

    usuarioApiMock.limparUsuarioAtual.mockClear();
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
        {
          provide: UsuarioApi,
          useValue: usuarioApiMock,
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
    expect(
      element.querySelector(
        '[data-testid="account-link"]'
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

    expect(usuarioApiMock.buscarAtual)
      .toHaveBeenCalledOnce();

    const overlay =
      overlayContainer.getContainerElement();

    expect(overlay.textContent)
      .toContain('Felipe');

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

    expect(
      overlay.querySelector(
        '[data-testid="mobile-account-link"]'
      )?.getAttribute('href')
    ).toBe('/conta');
    expect(
      overlay.querySelector(
        '[data-testid="mobile-admin-users-link"]'
      )
    ).toBeNull();

    const logoutButton = overlay.querySelector(
      '[data-testid="mobile-logout-button"]'
    ) as HTMLButtonElement;

    logoutButton.click();

    expect(authServiceMock.sair)
      .toHaveBeenCalledOnce();

  });

  it('should display the user menu and logout when authenticated', async () => {
    autenticado.set(true);

    usuario.set({
      subject: 'keycloak-user-id',
      nome: 'Felipe',
      email: 'felipe@email.com',
      roles: ['USUARIO'],
    });

    fixture.detectChanges();

    const element =
      fixture.nativeElement as HTMLElement;

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

    const accountMenuButton = element.querySelector(
      '[data-testid="desktop-account-menu-button"]'
    ) as HTMLButtonElement;

    expect(announceLink?.getAttribute('href'))
      .toBe('/veiculos/anunciar');

    expect(myAdsLink?.getAttribute('href'))
      .toBe('/veiculos/meus-anuncios');

    expect(myPurchasesLink?.getAttribute('href'))
      .toBe('/vendas/minhas-compras');

    expect(mySalesLink?.getAttribute('href'))
      .toBe('/vendas/minhas-vendas');

    expect(accountMenuButton.textContent)
      .toContain('Felipe');

    expect(
      element.querySelector(
        '[data-testid="login-button"]'
      )
    ).toBeNull();

    accountMenuButton.click();

    await fixture.whenStable();
    fixture.detectChanges();

    const overlay =
      overlayContainer.getContainerElement();

    expect(
      overlay.querySelector(
        '[data-testid="account-link"]'
      )?.getAttribute('href')
    ).toBe('/conta');

    const logoutButton = overlay.querySelector(
      '[data-testid="logout-button"]'
    ) as HTMLButtonElement;

    expect(logoutButton).not.toBeNull();

    logoutButton.click();

    expect(authServiceMock.sair)
      .toHaveBeenCalledOnce();
  });

  it('should update the displayed name from the local profile', () => {
    autenticado.set(true);

    usuario.set({
      subject: 'keycloak-user-id',
      nome: 'Nome do Keycloak',
      email: 'felipe@email.com',
      roles: ['USUARIO'],
    });

    fixture.detectChanges();

    const element =
      fixture.nativeElement as HTMLElement;

    const accountMenuButton = element.querySelector(
      '[data-testid="desktop-account-menu-button"]'
    ) as HTMLButtonElement;

    expect(accountMenuButton.textContent)
      .toContain('Felipe');

    perfilLocal.set({
      ...usuarioLocal,
      nome: 'Felipe Jr',
    });

    fixture.detectChanges();

    expect(accountMenuButton.textContent)
      .toContain('Felipe Jr');

    expect(accountMenuButton.textContent)
      .not.toContain('Nome do Keycloak');
  });

  it('should display admin navigation only for an administrator', async () => {
    autenticado.set(true);

    usuario.set({
      subject: 'admin-keycloak-id',
      nome: 'Administrador',
      email: 'admin@email.com',
      roles: ['ADMIN'],
    });

    authServiceMock.possuiRole.mockImplementation(
      role => role === 'ADMIN'
    );

    fixture.detectChanges();

    const element =
      fixture.nativeElement as HTMLElement;

    const accountMenuButton = element.querySelector(
      '[data-testid="desktop-account-menu-button"]'
    ) as HTMLButtonElement;

    accountMenuButton.click();

    await fixture.whenStable();
    fixture.detectChanges();

    let overlay =
      overlayContainer.getContainerElement();

    expect(
      overlay.querySelector(
        '[data-testid="admin-users-link"]'
      )?.getAttribute('href')
    ).toBe('/admin/usuarios');

    const mobileMenuButton = element.querySelector(
      '[data-testid="mobile-menu-button"]'
    ) as HTMLButtonElement;

    mobileMenuButton.click();

    await fixture.whenStable();
    fixture.detectChanges();

    overlay = overlayContainer.getContainerElement();

    expect(
      overlay.querySelector(
        '[data-testid="mobile-admin-users-link"]'
      )?.getAttribute('href')
    ).toBe('/admin/usuarios');
  });
});