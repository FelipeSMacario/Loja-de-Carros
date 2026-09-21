import { HttpErrorResponse } from '@angular/common/http';
import { signal } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import {
    ActivatedRouteSnapshot,
    CanActivateFn,
    provideRouter,
    Router,
    RouterStateSnapshot,
    UrlTree,
} from '@angular/router';
import {
    firstValueFrom,
    isObservable,
    of,
    throwError,
} from 'rxjs';
import { vi } from 'vitest';

import {
    UsuarioApi,
    UsuarioAtualResponse,
} from '../data-access/usuario-api';
import {
    cadastroPendenteGuard,
    perfilCompletoGuard,
} from './perfil-guards';

describe('perfil guards', () => {
    const usuario: UsuarioAtualResponse = {
        id: 3,
        nome: 'Steven Seagal',
        cpf: '12345678901',
        dataNascimento: '1952-04-10',
        email: 'steven@email.com',
        ativo: true,
    };

    const usuarioAtual =
        signal<UsuarioAtualResponse | null>(null);

    const usuarioApiMock = {
        usuarioAtual: usuarioAtual.asReadonly(),
        buscarAtual: vi.fn(),
    };

    let router: Router;

    beforeEach(() => {
        usuarioAtual.set(null);

        usuarioApiMock.buscarAtual
            .mockReset()
            .mockReturnValue(of(usuario));

        TestBed.configureTestingModule({
            providers: [
                provideRouter([]),
                {
                    provide: UsuarioApi,
                    useValue: usuarioApiMock,
                },
            ],
        });

        router = TestBed.inject(Router);
    });

    async function executarGuard(
        guard: CanActivateFn,
        url: string
    ) {
        const resultado = TestBed.runInInjectionContext(
            () => guard(
                {} as ActivatedRouteSnapshot,
                { url } as RouterStateSnapshot
            )
        );

        if (isObservable(resultado)) {
            return firstValueFrom(resultado);
        }

        return resultado;
    }

    it('should allow access when the local profile is cached', async () => {
        usuarioAtual.set(usuario);

        const resultado = await executarGuard(
            perfilCompletoGuard,
            '/conta'
        );

        expect(resultado).toBe(true);

        expect(usuarioApiMock.buscarAtual)
            .not.toHaveBeenCalled();
    });

    it('should load the profile and allow access', async () => {
        const resultado = await executarGuard(
            perfilCompletoGuard,
            '/veiculos/meus-anuncios'
        );

        expect(usuarioApiMock.buscarAtual)
            .toHaveBeenCalledOnce();

        expect(resultado).toBe(true);
    });

    it('should redirect a user without profile to registration', async () => {
        usuarioApiMock.buscarAtual.mockReturnValue(
            throwError(() =>
                new HttpErrorResponse({
                    status: 403,
                    statusText: 'Forbidden',
                    error: {
                        message:
                            'Usuário autenticado não possui cadastro local.',
                    },
                })
            )
        );

        const resultado = await executarGuard(
            perfilCompletoGuard,
            '/vendas/minhas-compras'
        );

        expect(resultado).toBeInstanceOf(UrlTree);

        expect(
            router.serializeUrl(resultado as UrlTree)
        ).toBe(
            '/completar-cadastro'
            + '?returnUrl=%2Fvendas%2Fminhas-compras'
        );
    });

    it('should not treat an unavailable API as a missing profile', async () => {
        usuarioApiMock.buscarAtual.mockReturnValue(
            throwError(() =>
                new HttpErrorResponse({
                    status: 500,
                    statusText: 'Internal Server Error',
                })
            )
        );

        const resultado = await executarGuard(
            perfilCompletoGuard,
            '/conta'
        );

        expect(resultado).toBe(true);
    });

    it('should redirect registration when a cached profile exists', async () => {
        usuarioAtual.set(usuario);

        const resultado = await executarGuard(
            cadastroPendenteGuard,
            '/completar-cadastro'
        );

        expect(resultado).toBeInstanceOf(UrlTree);

        expect(
            router.serializeUrl(resultado as UrlTree)
        ).toBe('/home');

        expect(usuarioApiMock.buscarAtual)
            .not.toHaveBeenCalled();
    });

    it('should redirect registration when the API finds a profile', async () => {
        const resultado = await executarGuard(
            cadastroPendenteGuard,
            '/completar-cadastro'
        );

        expect(usuarioApiMock.buscarAtual)
            .toHaveBeenCalledOnce();

        expect(resultado).toBeInstanceOf(UrlTree);

        expect(
            router.serializeUrl(resultado as UrlTree)
        ).toBe('/home');
    });

    it('should allow registration when the profile does not exist', async () => {
        usuarioApiMock.buscarAtual.mockReturnValue(
            throwError(() =>
                new HttpErrorResponse({
                    status: 403,
                    statusText: 'Forbidden',
                    error: {
                        message:
                            'Usuário autenticado não possui cadastro local.',
                    },
                })
            )
        );

        const resultado = await executarGuard(
            cadastroPendenteGuard,
            '/completar-cadastro'
        );

        expect(resultado).toBe(true);
    });
});