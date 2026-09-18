import { provideHttpClient } from '@angular/common/http';
import {
    HttpTestingController,
    provideHttpClientTesting,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { firstValueFrom } from 'rxjs';

import { environment } from '../../../../environments/environment';
import { ImagemResponse } from '../models/imagem-response';
import { ImagemApi } from './imagem-api';

describe('ImagemApi', () => {
    let service: ImagemApi;
    let httpTesting: HttpTestingController;

    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [
                provideHttpClient(),
                provideHttpClientTesting(),
            ],
        });

        service = TestBed.inject(ImagemApi);
        httpTesting = TestBed.inject(HttpTestingController);
    });

    afterEach(() => {
        httpTesting.verify();
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });

    it('should list vehicle images', async () => {
        const imagens: ImagemResponse[] = [
            {
                id: 10,
                nomeOriginal: 'frente.jpg',
                objectKey: '1/frente.jpg',
                principal: true,
            },
        ];

        const resultadoPromise = firstValueFrom(
            service.listarPorVeiculo(1)
        );

        const request = httpTesting.expectOne(
            `${environment.apiUrl}/veiculos/1/imagens`
        );

        expect(request.request.method).toBe('GET');

        request.flush(imagens);

        expect(await resultadoPromise).toEqual(imagens);
    });

    it('should add images to a vehicle', async () => {
        const arquivos = [
            new File(['frente'], 'frente.jpg', {
                type: 'image/jpeg',
            }),
            new File(['traseira'], 'traseira.jpg', {
                type: 'image/jpeg',
            }),
        ];

        const imagens: ImagemResponse[] = [
            {
                id: 10,
                nomeOriginal: 'frente.jpg',
                objectKey: '1/frente.jpg',
                principal: true,
            },
            {
                id: 11,
                nomeOriginal: 'traseira.jpg',
                objectKey: '1/traseira.jpg',
                principal: false,
            },
        ];

        const resultadoPromise = firstValueFrom(
            service.adicionarAoVeiculo(1, arquivos)
        );

        const request = httpTesting.expectOne(
            `${environment.apiUrl}/veiculos/1/imagens`
        );

        expect(request.request.method).toBe('POST');
        expect(request.request.body).toBeInstanceOf(FormData);

        const formData = request.request.body as FormData;

        expect(formData.getAll('files')).toEqual(arquivos);

        request.flush(imagens);

        expect(await resultadoPromise).toEqual(imagens);
    });

    it('should delete a vehicle image', async () => {
        const resultadoPromise = firstValueFrom(
            service.excluir(10)
        );

        const request = httpTesting.expectOne(
            `${environment.apiUrl}/imagens/10`
        );

        expect(request.request.method).toBe('DELETE');

        request.flush(null, {
            status: 204,
            statusText: 'No Content',
        });

        await resultadoPromise;
    });

    it('should set a vehicle image as principal', async () => {
        const resultadoPromise = firstValueFrom(
            service.definirPrincipal(10)
        );

        const request = httpTesting.expectOne(
            `${environment.apiUrl}/imagens/10/principal`
        );

        expect(request.request.method).toBe('PATCH');
        expect(request.request.body).toBeNull();

        request.flush(null, {
            status: 204,
            statusText: 'No Content',
        });

        await resultadoPromise;
    });
});