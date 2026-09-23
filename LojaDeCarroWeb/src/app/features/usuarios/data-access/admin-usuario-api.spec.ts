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
    UsuarioAtualResponse,
} from './usuario-api';
import { AdminUsuarioApi } from './admin-usuario-api';

describe('AdminUsuarioApi', () => {
    let api: AdminUsuarioApi;
    let httpTesting: HttpTestingController;

    const usuario: UsuarioAtualResponse = {
        id: 4,
        nome: 'Jean-Claude Van Damme',
        cpf: '12345678901',
        dataNascimento: '1960-10-18',
        email: 'van.damme@email.com',
        ativo: false,
    };

    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [
                provideHttpClient(),
                provideHttpClientTesting(),
            ],
        });

        api = TestBed.inject(AdminUsuarioApi);
        httpTesting = TestBed.inject(
            HttpTestingController
        );
    });

    afterEach(() => {
        httpTesting.verify();
    });

    it('should list users using the selected status', async () => {
        const resultado = firstValueFrom(
            api.listar('INATIVAS')
        );

        const request = httpTesting.expectOne(
            requisicao =>
                requisicao.url
                === `${environment.apiUrl}/admin/usuarios`
                && requisicao.params.get('status')
                === 'INATIVAS'
        );

        expect(request.request.method).toBe('GET');

        request.flush([usuario]);

        expect(await resultado).toEqual([usuario]);
    });

    it('should use all users as the default filter', async () => {
        const resultado = firstValueFrom(
            api.listar()
        );

        const request = httpTesting.expectOne(
            requisicao =>
                requisicao.url
                === `${environment.apiUrl}/admin/usuarios`
                && requisicao.params.get('status')
                === 'TODAS'
        );

        expect(request.request.method).toBe('GET');

        request.flush([usuario]);

        expect(await resultado).toEqual([usuario]);
    });

    it('should load a user by id', async () => {
        const resultado = firstValueFrom(
            api.buscarPorId(usuario.id)
        );

        const request = httpTesting.expectOne(
            `${environment.apiUrl}/admin/usuarios/${usuario.id}`
        );

        expect(request.request.method).toBe('GET');

        request.flush(usuario);

        expect(await resultado).toEqual(usuario);
    });

    it('should change the user status', async () => {
        const usuarioReativado = {
            ...usuario,
            ativo: true,
        };

        const resultado = firstValueFrom(
            api.alterarStatus(usuario.id, true)
        );

        const request = httpTesting.expectOne(
            `${environment.apiUrl}/admin/usuarios/${usuario.id}/status`
        );

        expect(request.request.method).toBe('PATCH');
        expect(request.request.body).toEqual({
            ativo: true,
        });

        request.flush(usuarioReativado);

        expect(await resultado)
            .toEqual(usuarioReativado);
    });
});