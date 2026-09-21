import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting, } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { firstValueFrom } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { UsuarioApi, UsuarioAtualResponse, UsuarioUpdateRequest, UsuarioCadastroRequest, } from './usuario-api';

describe('UsuarioApi', () => {
  let api: UsuarioApi;
  let httpTesting: HttpTestingController;

  const usuario: UsuarioAtualResponse = {
    id: 3,
    nome: 'Steven Seagal',
    cpf: '12345678901',
    dataNascimento: '1952-04-10',
    email: 'steven@email.com',
    ativo: true,
  };


  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });

    api = TestBed.inject(UsuarioApi);
    httpTesting = TestBed.inject(
      HttpTestingController
    );
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it('should load the authenticated user', async () => {
    const resultado = firstValueFrom(
      api.buscarAtual()
    );

    const request = httpTesting.expectOne(
      `${environment.apiUrl}/usuarios/me`
    );

    expect(request.request.method).toBe('GET');

    request.flush(usuario);

    expect(await resultado).toEqual(usuario);

    expect(api.usuarioAtual()).toEqual(usuario);
  });

  it('should deactivate the authenticated user', async () => {
    const usuarioDesativado: UsuarioAtualResponse = {
      ...usuario,
      ativo: false,
    };

    const resultado = firstValueFrom(
      api.desativar()
    );

    const request = httpTesting.expectOne(
      `${environment.apiUrl}/usuarios/me/desativar`
    );

    expect(request.request.method).toBe('PATCH');
    expect(request.request.body).toBeNull();

    request.flush(usuarioDesativado);

    expect(await resultado)
      .toEqual(usuarioDesativado);

    expect(api.usuarioAtual())
      .toEqual(usuarioDesativado);
  });
  it('should update the authenticated user', async () => {
    const alteracao: UsuarioUpdateRequest = {
      nome: 'Steven Seagal Silva',
      dataNascimento: '1952-04-10',
    };

    const usuarioAtualizado: UsuarioAtualResponse = {
      ...usuario,
      ...alteracao,
    };

    const resultado = firstValueFrom(
      api.atualizar(alteracao)
    );

    const request = httpTesting.expectOne(
      `${environment.apiUrl}/usuarios/me`
    );

    expect(request.request.method).toBe('PUT');
    expect(request.request.body).toEqual(alteracao);

    request.flush(usuarioAtualizado);

    expect(await resultado)
      .toEqual(usuarioAtualizado);

    expect(api.usuarioAtual())
      .toEqual(usuarioAtualizado);
  });
  it('should clear the current user state', async () => {
    const resultado = firstValueFrom(
      api.buscarAtual()
    );

    const request = httpTesting.expectOne(
      `${environment.apiUrl}/usuarios/me`
    );

    request.flush(usuario);

    await resultado;

    expect(api.usuarioAtual()).toEqual(usuario);

    api.limparUsuarioAtual();

    expect(api.usuarioAtual()).toBeNull();
  });
  it('should create the authenticated user profile', async () => {
    const cadastro: UsuarioCadastroRequest = {
      nome: 'Chuck Norris',
      cpf: '15152736999',
      dataNascimento: '1940-03-10',
    };

    const usuarioCriado: UsuarioAtualResponse = {
      id: 4,
      ...cadastro,
      email: 'chuck@email.com',
      ativo: true,
    };

    const resultado = firstValueFrom(
      api.criar(cadastro)
    );

    const request = httpTesting.expectOne(
      `${environment.apiUrl}/usuarios`
    );

    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual(cadastro);

    request.flush(usuarioCriado, {
      status: 201,
      statusText: 'Created',
    });

    expect(await resultado)
      .toEqual(usuarioCriado);

    expect(api.usuarioAtual())
      .toEqual(usuarioCriado);
  });
});