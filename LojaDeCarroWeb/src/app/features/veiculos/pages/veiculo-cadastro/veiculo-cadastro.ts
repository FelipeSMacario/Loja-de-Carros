import { ChangeDetectionStrategy, Component, inject, OnInit, signal, } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators, } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { Router } from '@angular/router';
import { finalize } from 'rxjs';
import { VeiculoApi } from '../../data-access/veiculo-api';
import { VeiculoRequest } from '../../models/veiculo-request';
import { VeiculoCatalogoApi } from '../../data-access/veiculo-catalogo-api';
import { VeiculoCatalogos } from '../../models/veiculo-catalogos';

@Component({
  selector: 'app-veiculo-cadastro',
  imports: [
    ReactiveFormsModule,
    MatButtonModule,
    MatCheckboxModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatProgressSpinnerModule,
    MatSelectModule,
  ],
  templateUrl: './veiculo-cadastro.html',
  styleUrl: './veiculo-cadastro.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VeiculoCadastro implements OnInit {
  private readonly formBuilder = inject(FormBuilder);
  private readonly catalogoApi =
    inject(VeiculoCatalogoApi);

  readonly anoAtual = new Date().getFullYear();

  readonly catalogos =
    signal<VeiculoCatalogos | null>(null);

  readonly carregandoCatalogos = signal(true);
  readonly erroCatalogos = signal(false);
  private readonly veiculoApi = inject(VeiculoApi);
  private readonly router = inject(Router);

  readonly formulario = this.formBuilder.nonNullable.group({
    placa: [
      '',
      [
        Validators.required,
        Validators.pattern(
          /^[A-Z]{3}\d[A-Z\d]\d{2}$/
        ),
      ],
    ],
    anoFabricacao: [
      this.anoAtual,
      [
        Validators.required,
        Validators.min(1886),
        Validators.max(this.anoAtual + 1),
      ],
    ],
    quilometragem: [
      0,
      [
        Validators.required,
        Validators.min(0),
      ],
    ],
    valor: [
      0,
      [
        Validators.required,
        Validators.min(0.01),
      ],
    ],
    motor: [
      '',
      [
        Validators.required,
        Validators.maxLength(255),
      ],
    ],
    descricao: [
      '',
      [
        Validators.required,
        Validators.maxLength(255),
      ],
    ],
    idModelo: [
      0,
      [
        Validators.required,
        Validators.min(1),
      ],
    ],
    idCarroceria: [
      0,
      [
        Validators.required,
        Validators.min(1),
      ],
    ],
    idCor: [
      0,
      [
        Validators.required,
        Validators.min(1),
      ],
    ],
    idCombustivel: [
      0,
      [
        Validators.required,
        Validators.min(1),
      ],
    ],
    idsOpcionais:
      this.formBuilder.nonNullable.control<number[]>([]),
  });

  readonly limiteImagens = 10;

  readonly arquivosSelecionados =
    signal<readonly File[]>([]);

  readonly erroArquivos =
    signal<string | null>(null);

  readonly enviando = signal(false);
  readonly erroCadastro = signal(false);
  ngOnInit(): void {
    this.carregarCatalogos();
  }

  carregarCatalogos(): void {
    this.carregandoCatalogos.set(true);
    this.erroCatalogos.set(false);

    this.catalogoApi.carregar()
      .subscribe({
        next: catalogos => {
          this.catalogos.set(catalogos);
          this.carregandoCatalogos.set(false);
        },
        error: () => {
          this.catalogos.set(null);
          this.carregandoCatalogos.set(false);
          this.erroCatalogos.set(true);
        },
      });
  }

  selecionarArquivos(event: Event): void {
    const input = event.target as HTMLInputElement;
    const arquivos = Array.from(input.files ?? []);

    this.erroArquivos.set(null);

    if (arquivos.length > this.limiteImagens) {
      this.erroArquivos.set(
        `Selecione no máximo ${this.limiteImagens} imagens.`
      );

      this.arquivosSelecionados.set(
        arquivos.slice(0, this.limiteImagens)
      );

      return;
    }

    this.arquivosSelecionados.set(arquivos);
  }

  removerArquivo(indice: number): void {
    this.arquivosSelecionados.update(arquivos =>
      arquivos.filter(
        (_arquivo, indiceAtual) =>
          indiceAtual !== indice
      )
    );

    this.erroArquivos.set(null);
  }

  salvar(): void {
    if (this.formulario.invalid || this.enviando()) {
      this.formulario.markAllAsTouched();
      return;
    }

    const request: VeiculoRequest =
      this.formulario.getRawValue();

    this.enviando.set(true);
    this.erroCadastro.set(false);

    this.veiculoApi.criar(
      request,
      this.arquivosSelecionados()
    )
      .pipe(
        finalize(() => {
          this.enviando.set(false);
        })
      )
      .subscribe({
        next: veiculo => {
          void this.router.navigate([
            '/veiculos',
            veiculo.id,
          ]);
        },
        error: () => {
          this.erroCadastro.set(true);
        },
      });
  }
}