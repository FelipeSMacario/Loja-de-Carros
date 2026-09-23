import {
  ComponentFixture,
  TestBed,
} from '@angular/core/testing';
import {
  MAT_DIALOG_DATA,
  MatDialogRef,
} from '@angular/material/dialog';
import { vi } from 'vitest';

import {
  DialogoConfirmacao,
  DialogoConfirmacaoData,
} from './dialogo-confirmacao';

describe('DialogoConfirmacao', () => {
  let component: DialogoConfirmacao;
  let fixture: ComponentFixture<DialogoConfirmacao>;

  const dialogRefMock = {
    close: vi.fn(),
  };

  const data: DialogoConfirmacaoData = {
    titulo: 'Excluir imagem?',
    mensagem: 'Essa ação não poderá ser desfeita.',
    textoConfirmar: 'Excluir',
    tipo: 'perigo',
  };

  beforeEach(async () => {
    vi.clearAllMocks();

    await TestBed.configureTestingModule({
      imports: [DialogoConfirmacao],
      providers: [
        {
          provide: MAT_DIALOG_DATA,
          useValue: data,
        },
        {
          provide: MatDialogRef,
          useValue: dialogRefMock,
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(
      DialogoConfirmacao
    );
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the confirmation information', () => {
    const element = fixture.nativeElement as HTMLElement;

    expect(element.textContent)
      .toContain('Excluir imagem?');

    expect(element.textContent)
      .toContain('Essa ação não poderá ser desfeita.');

    expect(element.textContent)
      .toContain('Excluir');
  });

  it('should close with true when confirmed', () => {
    component.confirmar();

    expect(dialogRefMock.close)
      .toHaveBeenCalledExactlyOnceWith(true);
  });

  it('should close with false when cancelled', () => {
    component.cancelar();

    expect(dialogRefMock.close)
      .toHaveBeenCalledExactlyOnceWith(false);
  });
});