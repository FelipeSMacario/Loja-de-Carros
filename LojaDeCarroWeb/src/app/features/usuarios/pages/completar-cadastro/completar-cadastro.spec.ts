import { HttpErrorResponse } from '@angular/common/http';
import { signal } from '@angular/core';
import {
  ComponentFixture,
  TestBed,
} from '@angular/core/testing';
import {
  ActivatedRoute,
  convertToParamMap,
  provideRouter,
  Router,
} from '@angular/router';
import {
  of,
  throwError,
} from 'rxjs';
import { vi } from 'vitest';

import {
  AuthService,
  UsuarioAutenticado,
} from '../../../../core/auth/auth-service';
import {
  UsuarioApi,
  UsuarioAtualResponse,
} from '../../data-access/usuario-api';
import {
  CompletarCadastro,
} from './completar-cadastro';

describe('CompletarCadastro', () => {
  let component: CompletarCadastro;
  let fixture: ComponentFixture<CompletarCadastro>;
  let router: Router;
  let navigateByUrlMock: ReturnType<typeof vi.spyOn>;

  const usuarioAutenticado =
    signal<UsuarioAutenticado | null>({
      subject: 'keycloak-user-id',
      nome: 'Chuck Norris',
      email: 'chuck@email.com',
      roles: ['USUARIO'],
    });

  const authServiceMock = {
    usuario: usuarioAutenticado,
  };

  const usuarioCriado: UsuarioAtualResponse = {
    id: 4,
    nome: 'Chuck Norris',
    cpf: '52998224725',
    dataNascimento: '1940-03-10',
    email: 'chuck@email.com',
    ativo: true,
  };

  const usuarioApiMock = {
    criar: vi.fn(),
  };

  const activatedRouteMock = {
    snapshot: {
      queryParamMap: convertToParamMap({
        returnUrl: '/vendas/minhas-compras',
      }),
    },
  };

  beforeEach(async () => {
    usuarioAutenticado.set({
      subject: 'keycloak-user-id',
      nome: 'Chuck Norris',
      email: 'chuck@email.com',
      roles: ['USUARIO'],
    });

    usuarioApiMock.criar
      .mockReset()
      .mockReturnValue(of(usuarioCriado));

    activatedRouteMock.snapshot.queryParamMap =
      convertToParamMap({
        returnUrl: '/vendas/minhas-compras',
      });

    await TestBed.configureTestingModule({
      imports: [CompletarCadastro],
      providers: [
        provideRouter([]),
        {
          provide: ActivatedRoute,
          useValue: activatedRouteMock,
        },
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

    router = TestBed.inject(Router);

    navigateByUrlMock = vi
      .spyOn(router, 'navigateByUrl')
      .mockResolvedValue(true);

    fixture = TestBed.createComponent(
      CompletarCadastro
    );

    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create and prefill authenticated user data', () => {
    const element =
      fixture.nativeElement as HTMLElement;

    expect(component).toBeTruthy();

    expect(component.formulario.controls.nome.value)
      .toBe('Chuck Norris');

    expect(component.emailAutenticado())
      .toBe('chuck@email.com');

    const nomeInput = element.querySelector(
      '[data-testid="registration-name-input"]'
    ) as HTMLInputElement;

    expect(nomeInput.value)
      .toBe('Chuck Norris');

    expect(element.textContent)
      .toContain('chuck@email.com');
  });

  it('should format and validate a CPF', () => {
    component.formatarCpf({
      target: {
        value: '52998224725',
      },
    } as unknown as Event);

    expect(component.formulario.controls.cpf.value)
      .toBe('529.982.247-25');

    expect(component.formulario.controls.cpf.valid)
      .toBe(true);
  });

  it('should not submit an invalid form', () => {
    component.formulario.setValue({
      nome: '   ',
      cpf: '111.111.111-11',
      dataNascimento: '',
    });

    component.concluirCadastro();

    expect(component.formulario.invalid).toBe(true);

    expect(component.formulario.controls.nome.touched)
      .toBe(true);

    expect(component.formulario.controls.cpf.hasError('cpf'))
      .toBe(true);

    expect(usuarioApiMock.criar)
      .not.toHaveBeenCalled();

    expect(navigateByUrlMock)
      .not.toHaveBeenCalled();
  });

  it('should create the profile and navigate to the return URL', () => {
    component.formulario.setValue({
      nome: '  Chuck Norris  ',
      cpf: '529.982.247-25',
      dataNascimento: '1940-03-10',
    });

    component.concluirCadastro();

    expect(usuarioApiMock.criar)
      .toHaveBeenCalledExactlyOnceWith({
        nome: 'Chuck Norris',
        cpf: '52998224725',
        dataNascimento: '1940-03-10',
      });

    expect(navigateByUrlMock)
      .toHaveBeenCalledExactlyOnceWith(
        '/vendas/minhas-compras'
      );

    expect(component.enviando()).toBe(false);
    expect(component.erroCadastro()).toBeNull();
  });

  it('should display the backend error', () => {
    const mensagem =
      'O CPF informado já possui um cadastro.';

    usuarioApiMock.criar.mockReturnValue(
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

    component.formulario.setValue({
      nome: 'Chuck Norris',
      cpf: '529.982.247-25',
      dataNascimento: '1940-03-10',
    });

    component.concluirCadastro();
    fixture.detectChanges();

    expect(component.enviando()).toBe(false);

    expect(component.erroCadastro())
      .toBe(mensagem);

    expect(navigateByUrlMock)
      .not.toHaveBeenCalled();

    expect(
      fixture.nativeElement.querySelector(
        '[data-testid="registration-error"]'
      )?.textContent
    ).toContain(mensagem);
  });

  it('should reject an external return URL', () => {
    activatedRouteMock.snapshot.queryParamMap =
      convertToParamMap({
        returnUrl: '//site-malicioso.com',
      });

    component.formulario.setValue({
      nome: 'Chuck Norris',
      cpf: '529.982.247-25',
      dataNascimento: '1940-03-10',
    });

    component.concluirCadastro();

    expect(navigateByUrlMock)
      .toHaveBeenCalledExactlyOnceWith('/home');
  });

  it('should ignore another submission while one is running', () => {
    component.enviando.set(true);

    component.formulario.setValue({
      nome: 'Chuck Norris',
      cpf: '529.982.247-25',
      dataNascimento: '1940-03-10',
    });

    component.concluirCadastro();

    expect(usuarioApiMock.criar)
      .not.toHaveBeenCalled();

    expect(navigateByUrlMock)
      .not.toHaveBeenCalled();
  });
});