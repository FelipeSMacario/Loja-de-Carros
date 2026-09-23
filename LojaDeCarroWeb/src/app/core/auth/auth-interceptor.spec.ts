import { TestBed, } from '@angular/core/testing';
import { HttpClient, provideHttpClient, withInterceptors, } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting, } from '@angular/common/http/testing';
import { vi } from 'vitest';
import { firstValueFrom } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthService } from './auth-service';
import { authInterceptor } from './auth-interceptor';

describe('authInterceptor', () => {
  let http: HttpClient;
  let httpTesting: HttpTestingController;

  const authServiceMock = {
    obterTokenValido: vi.fn(),
  };

  beforeEach(() => {
    authServiceMock.obterTokenValido.mockReset();

    TestBed.configureTestingModule({
      providers: [
        {
          provide: AuthService,
          useValue: authServiceMock,
        },
        provideHttpClient(
          withInterceptors([authInterceptor])
        ),
        provideHttpClientTesting(),
      ],
    });

    http = TestBed.inject(HttpClient);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it('should add the bearer token to API requests', async () => {
    authServiceMock.obterTokenValido
      .mockResolvedValue('token-valido');

    const resultadoPromise = firstValueFrom(
      http.get(`${environment.apiUrl}/usuarios`)
    );

    await Promise.resolve();

    const request = httpTesting.expectOne(
      `${environment.apiUrl}/usuarios`
    );

    expect(
      request.request.headers.get('Authorization')
    ).toBe('Bearer token-valido');

    request.flush({});

    expect(await resultadoPromise).toEqual({});
  });

  it('should send API requests without a token for visitors', async () => {
    authServiceMock.obterTokenValido
      .mockResolvedValue(null);

    const resultadoPromise = firstValueFrom(
      http.get(`${environment.apiUrl}/veiculos`)
    );

    await Promise.resolve();

    const request = httpTesting.expectOne(
      `${environment.apiUrl}/veiculos`
    );

    expect(
      request.request.headers.has('Authorization')
    ).toBe(false);

    request.flush({});

    expect(await resultadoPromise).toEqual({});
  });

  it('should not intercept requests to another server', () => {
    http.get('https://example.com/recurso')
      .subscribe();

    const request = httpTesting.expectOne(
      'https://example.com/recurso'
    );

    expect(
      authServiceMock.obterTokenValido
    ).not.toHaveBeenCalled();

    expect(
      request.request.headers.has('Authorization')
    ).toBe(false);

    request.flush({});
  });
});