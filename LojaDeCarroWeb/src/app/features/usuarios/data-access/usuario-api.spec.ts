import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { firstValueFrom } from 'rxjs';

import { environment } from
  '../../../../environments/environment';
import {
  UsuarioApi,
  UsuarioAtualResponse,
} from './usuario-api';

describe('UsuarioApi', () => {
  let api: UsuarioApi;
  let httpTesting: HttpTestingController;

  const usuario: UsuarioAtualResponse = {
    id: 3,
    nome: 'Steven Seagal',
    cpf: '12345678901',
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
  });
});