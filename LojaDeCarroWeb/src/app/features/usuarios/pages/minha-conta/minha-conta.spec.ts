import { HttpErrorResponse } from '@angular/common/http';
import {
  ComponentFixture,
  TestBed,
} from '@angular/core/testing';
import { MatDialog } from '@angular/material/dialog';
import { of, throwError } from 'rxjs';
import { vi } from 'vitest';

import { AuthService } from '../../../../core/auth/auth-service';
import {
  UsuarioApi,
  UsuarioAtualResponse,
} from '../../data-access/usuario-api';
import { MinhaConta } from './minha-conta';

describe('MinhaConta', () => {
  let component: MinhaConta;
  let fixture: ComponentFixture<MinhaConta>;

  const usuario: UsuarioAtualResponse = {
    id: 3,
    nome: 'Steven Seagal',
    cpf: '12345678901',
    email: 'steven@email.com',
    ativo: true,
  };

  const usuarioApiMock = {
    buscarAtual: vi.fn(),
    desativar: vi.fn(),
  };

  const authServiceMock = {
    sair: vi.fn(),
  };

  const dialogMock = {
    open: vi.fn(),
  };

  beforeEach(async () => {
    usuarioApiMock.buscarAtual
      .mockReset()
      .mockReturnValue(of(usuario));

    usuarioApiMock.desativar
      .mockReset()
      .mockReturnValue(
        of({
          ...usuario,
          ativo: false,
        })
      );

    authServiceMock.sair
      .mockReset()
      .mockResolvedValue(undefined);

    dialogMock.open
      .mockReset()
      .mockReturnValue({
        afterClosed: () => of(true),
      });

    await TestBed.configureTestingModule({
      imports: [MinhaConta],
      providers: [
        {
          provide: UsuarioApi,
          useValue: usuarioApiMock,
        },
        {
          provide: AuthService,
          useValue: authServiceMock,
        },
        {
          provide: MatDialog,
          useValue: dialogMock,
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(MinhaConta);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load and display the authenticated user', () => {
    const element = fixture.nativeElement as HTMLElement;

    expect(usuarioApiMock.buscarAtual)
      .toHaveBeenCalledOnce();

    expect(component.usuario()).toEqual(usuario);
    expect(component.carregando()).toBe(false);
    expect(component.erroCarregamento()).toBe(false);

    expect(element.textContent)
      .toContain('Steven Seagal');

    expect(element.textContent)
      .toContain('steven@email.com');

    expect(element.textContent)
      .toContain('***.***.789-01');

    expect(element.textContent)
      .not.toContain('12345678901');
      
  });

  it('should display an error when the account cannot be loaded', () => {
    usuarioApiMock.buscarAtual.mockReturnValue(
      throwError(() => new Error('Erro da API'))
    );

    component.carregar();
    fixture.detectChanges();

    const element = fixture.nativeElement as HTMLElement;

    expect(component.usuario()).toBeNull();
    expect(component.carregando()).toBe(false);
    expect(component.erroCarregamento()).toBe(true);

    expect(
      element.querySelector(
        '[data-testid="error-state"]'
      )
    ).not.toBeNull();
  });

  it('should keep the account when confirmation is dismissed', () => {
    dialogMock.open.mockReturnValue({
      afterClosed: () => of(false),
    });

    component.solicitarDesativacao();

    expect(dialogMock.open)
      .toHaveBeenCalledOnce();

    expect(usuarioApiMock.desativar)
      .not.toHaveBeenCalled();

    expect(authServiceMock.sair)
      .not.toHaveBeenCalled();
  });

  it('should deactivate the account and logout after confirmation', async () => {
    component.solicitarDesativacao();

    await fixture.whenStable();

    expect(dialogMock.open)
      .toHaveBeenCalledOnce();

    expect(usuarioApiMock.desativar)
      .toHaveBeenCalledOnce();

    expect(authServiceMock.sair)
      .toHaveBeenCalledOnce();

    expect(component.desativando()).toBe(false);
    expect(component.erroDesativacao()).toBeNull();
  });

  it('should display the backend message when deactivation is blocked', () => {
    const mensagem =
      'Não é possível desativar o usuário '
      + 'com uma ou mais compras em andamento.';

    usuarioApiMock.desativar.mockReturnValue(
      throwError(() =>
        new HttpErrorResponse({
          status: 400,
          statusText: 'Bad Request',
          error: {
            detail: mensagem,
          },
        })
      )
    );

    component.solicitarDesativacao();
    fixture.detectChanges();

    const element = fixture.nativeElement as HTMLElement;

    expect(usuarioApiMock.desativar)
      .toHaveBeenCalledOnce();

    expect(authServiceMock.sair)
      .not.toHaveBeenCalled();

    expect(component.desativando()).toBe(false);
    expect(component.erroDesativacao())
      .toBe(mensagem);

    expect(
      element.querySelector(
        '[data-testid="deactivation-error"]'
      )?.textContent
    ).toContain(mensagem);
  });
});