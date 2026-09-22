import {
  ComponentFixture,
  TestBed,
} from '@angular/core/testing';
import { MatDialog } from '@angular/material/dialog';
import {
  of,
  throwError,
} from 'rxjs';
import { vi } from 'vitest';

import {
  AdminUsuarioApi,
} from '../../data-access/admin-usuario-api';
import {
  UsuarioAtualResponse,
} from '../../data-access/usuario-api';
import { AdminUsuarios } from './admin-usuarios';

describe('AdminUsuarios', () => {
  let component: AdminUsuarios;
  let fixture: ComponentFixture<AdminUsuarios>;

  const usuarioAtivo: UsuarioAtualResponse = {
    id: 3,
    nome: 'Steven Seagal',
    cpf: '12345678901',
    dataNascimento: '1952-04-10',
    email: 'steven@email.com',
    ativo: true,
  };

  const usuarioInativo: UsuarioAtualResponse = {
    id: 4,
    nome: 'Jean-Claude Van Damme',
    cpf: '98765432100',
    dataNascimento: '1960-10-18',
    email: 'van.damme@email.com',
    ativo: false,
  };

  const adminUsuarioApiMock = {
    listar: vi.fn(),
    alterarStatus: vi.fn(),
  };

  const dialogMock = {
    open: vi.fn(),
  };

  beforeEach(async () => {
    adminUsuarioApiMock.listar
      .mockReset()
      .mockReturnValue(
        of([usuarioAtivo, usuarioInativo])
      );

    adminUsuarioApiMock.alterarStatus
      .mockReset()
      .mockReturnValue(
        of({
          ...usuarioInativo,
          ativo: true,
        })
      );

    dialogMock.open
      .mockReset()
      .mockReturnValue({
        afterClosed: () => of(true),
      });

    await TestBed.configureTestingModule({
      imports: [AdminUsuarios],
      providers: [
        {
          provide: AdminUsuarioApi,
          useValue: adminUsuarioApiMock,
        },
        {
          provide: MatDialog,
          useValue: dialogMock,
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(AdminUsuarios);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create and load all users', () => {
    expect(component).toBeTruthy();

    expect(adminUsuarioApiMock.listar)
      .toHaveBeenCalledExactlyOnceWith('TODAS');

    expect(component.usuarios()).toEqual([
      usuarioAtivo,
      usuarioInativo,
    ]);

    expect(component.carregando()).toBe(false);
    expect(component.erroCarregamento()).toBe(false);
  });

  it('should filter inactive users', () => {
    adminUsuarioApiMock.listar.mockClear();

    component.alterarFiltro('INATIVAS');

    expect(component.filtroSelecionado())
      .toBe('INATIVAS');

    expect(adminUsuarioApiMock.listar)
      .toHaveBeenCalledExactlyOnceWith('INATIVAS');
  });

  it('should keep the status when confirmation is dismissed', () => {
    dialogMock.open.mockReturnValue({
      afterClosed: () => of(false),
    });

    component.solicitarAlteracaoStatus(
      usuarioAtivo
    );

    expect(dialogMock.open).toHaveBeenCalledOnce();

    expect(adminUsuarioApiMock.alterarStatus)
      .not.toHaveBeenCalled();
  });

  it('should reactivate an inactive user', () => {
    adminUsuarioApiMock.listar.mockClear();

    component.solicitarAlteracaoStatus(
      usuarioInativo
    );

    expect(adminUsuarioApiMock.alterarStatus)
      .toHaveBeenCalledExactlyOnceWith(
        usuarioInativo.id,
        true
      );

    expect(adminUsuarioApiMock.listar)
      .toHaveBeenCalledExactlyOnceWith('TODAS');

    expect(component.acaoEmAndamentoId())
      .toBeNull();

    expect(component.erroAcao()).toBe(false);
  });

  it('should display an error when users cannot be loaded', () => {
    adminUsuarioApiMock.listar.mockReturnValue(
      throwError(() => new Error('Erro da API'))
    );

    component.carregar();

    expect(component.usuarios()).toEqual([]);
    expect(component.carregando()).toBe(false);
    expect(component.erroCarregamento()).toBe(true);
  });

  it('should display an error when status cannot be changed', () => {
    adminUsuarioApiMock.alterarStatus
      .mockReturnValue(
        throwError(() => new Error('Erro da API'))
      );

    component.solicitarAlteracaoStatus(
      usuarioAtivo
    );

    expect(component.acaoEmAndamentoId())
      .toBeNull();

    expect(component.erroAcao()).toBe(true);
  });
});