import {
  ChangeDetectionStrategy,
  Component,
  computed,
  DestroyRef,
  inject,
  OnInit,
  output,
  signal,
} from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  ReactiveFormsModule,
  ValidationErrors,
  ValidatorFn,
  Validators,
} from '@angular/forms';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { finalize } from 'rxjs';

import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';

import { VeiculoCatalogoApi } from '../../data-access/veiculo-catalogo-api';
import { CatalogoItem, VeiculoFiltroCatalogos } from '../../models/veiculo-catalogos';
import { VeiculoFiltro } from '../../models/veiculo-filtro';

function intervaloValido(campoMinimo: string, campoMaximo: string, nomeErro: string): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const minimo = control.get(campoMinimo)?.value;
    const maximo = control.get(campoMaximo)?.value;

    if (minimo == null || maximo == null) {
      return null;
    }

    return minimo > maximo ? { [nomeErro]: true } : null;
  };
}

@Component({
  selector: 'app-veiculo-filtros',
  imports: [
    ReactiveFormsModule,
    MatButtonModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatSelectModule,
  ],
  templateUrl: './veiculo-filtros.html',
  styleUrl: './veiculo-filtros.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VeiculoFiltros implements OnInit {
  private readonly formBuilder = inject(FormBuilder);
  private readonly catalogoApi = inject(VeiculoCatalogoApi);
  private readonly destroyRef = inject(DestroyRef);

  readonly filtroAplicado = output<VeiculoFiltro>();

  readonly catalogos = signal<VeiculoFiltroCatalogos | null>(null);
  readonly carregandoCatalogos = signal(false);
  readonly erroCatalogos = signal<string | null>(null);

  readonly formulario = this.formBuilder.group(
    {
      marcaId: this.formBuilder.control<number | null>(null),
      modeloId: this.formBuilder.control<number | null>({
        value: null,
        disabled: true,
      }),
      carroceriaId: this.formBuilder.control<number | null>(null),
      corId: this.formBuilder.control<number | null>(null),
      combustivelId: this.formBuilder.control<number | null>(null),

      anoMin: this.formBuilder.control<number | null>(null, Validators.min(1886)),
      anoMax: this.formBuilder.control<number | null>(null, Validators.min(1886)),
      valorMin: this.formBuilder.control<number | null>(null, Validators.min(0)),
      valorMax: this.formBuilder.control<number | null>(null, Validators.min(0)),
      quilometragemMax: this.formBuilder.control<number | null>(null, Validators.min(0)),
    },
    {
      validators: [
        intervaloValido('anoMin', 'anoMax', 'intervaloAnoInvalido'),
        intervaloValido('valorMin', 'valorMax', 'intervaloValorInvalido'),
      ],
    },
  );

  private readonly marcaSelecionada = toSignal(this.formulario.controls.marcaId.valueChanges, {
    initialValue: this.formulario.controls.marcaId.value,
  });

  readonly marcas = computed(() => {
    const modelos = this.catalogos()?.modelos ?? [];
    const marcasPorId = new Map<number, CatalogoItem>();

    for (const modelo of modelos) {
      if (modelo.marca.ativo) {
        marcasPorId.set(modelo.marca.id, modelo.marca);
      }
    }

    return Array.from(marcasPorId.values()).sort((marcaA, marcaB) =>
      marcaA.nome.localeCompare(marcaB.nome),
    );
  });

  readonly modelosFiltrados = computed(() => {
    const marcaId = this.marcaSelecionada();

    if (marcaId == null) {
      return [];
    }

    return (this.catalogos()?.modelos ?? [])
      .filter((modelo) => modelo.ativo && modelo.marca.id === marcaId)
      .sort((modeloA, modeloB) => modeloA.nome.localeCompare(modeloB.nome));
  });

  constructor() {
    this.formulario.controls.marcaId.valueChanges
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((marcaId) => {
        const modeloControl = this.formulario.controls.modeloId;

        modeloControl.reset(null, {
          emitEvent: false,
        });

        if (marcaId == null) {
          modeloControl.disable({
            emitEvent: false,
          });
        } else {
          modeloControl.enable({
            emitEvent: false,
          });
        }
      });
  }

  ngOnInit(): void {
    this.carregarCatalogos();
  }

  aplicarFiltros(): void {
    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }

    const valores = this.formulario.getRawValue();

    const filtro = Object.fromEntries(
      Object.entries(valores).filter(([, valor]) => valor != null),
    ) as VeiculoFiltro;

    this.filtroAplicado.emit(filtro);
  }

  limparFiltros(): void {
    this.formulario.reset();
    this.filtroAplicado.emit({});
  }

  private carregarCatalogos(): void {
    this.carregandoCatalogos.set(true);
    this.erroCatalogos.set(null);

    this.catalogoApi
      .carregarFiltros()
      .pipe(finalize(() => this.carregandoCatalogos.set(false)))
      .subscribe({
        next: (catalogos) => {
          this.catalogos.set(catalogos);
        },
        error: () => {
          this.erroCatalogos.set('Não foi possível carregar as opções dos filtros.');
        },
      });
  }
}
