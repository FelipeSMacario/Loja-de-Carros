import {
  ComponentFixture,
  TestBed,
} from '@angular/core/testing';
import { vi } from 'vitest';

import { VendaResponse } from '../../models/venda-response';
import {
  ContextoVenda,
  VendaCard,
} from './venda-card';

describe('VendaCard', () => {
  let component: VendaCard;
  let fixture: ComponentFixture<VendaCard>;

  const venda: VendaResponse = {
    id: 7,
    valorVenda: 75990,
    statusVenda: 'EM_ANDAMENTO',
    dataVenda: '2026-09-18T12:00:00',
    veiculo: {
      id: 1,
      marca: 'Chevrolet',
      modelo: 'Onix',
      status: 'RESERVADO',
    },
    vendedor: {
      id: 2,
      nome: 'Felipe Vendedor',
    },
    comprador: {
      id: 3,
      nome: 'Steven Seagal',
    },
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VendaCard],
    }).compileComponents();

    fixture = TestBed.createComponent(VendaCard);
    component = fixture.componentInstance;
  });

  function renderizar(
    contexto: ContextoVenda,
    valor: VendaResponse = venda
  ): HTMLElement {
    fixture.componentRef.setInput('venda', valor);
    fixture.componentRef.setInput('contexto', contexto);
    fixture.detectChanges();

    return fixture.nativeElement as HTMLElement;
  }

  it('should create', () => {
    renderizar('COMPRA');

    expect(component).toBeTruthy();
  });

  it('should display the seller in a purchase', () => {
    const element = renderizar('COMPRA');

    expect(element.textContent)
      .toContain('Chevrolet');

    expect(element.textContent)
      .toContain('Onix');

    expect(element.textContent)
      .toContain('Felipe Vendedor');

    expect(
      element.querySelector(
        '[data-testid="cancel-sale-button"]'
      )
    ).not.toBeNull();

    expect(
      element.querySelector(
        '[data-testid="complete-sale-button"]'
      )
    ).toBeNull();
  });

  it('should emit the cancellation requested by the buyer', () => {
    const cancelar = vi.fn();

    component.cancelar.subscribe(cancelar);

    const element = renderizar('COMPRA');

    const button = element.querySelector(
      '[data-testid="cancel-sale-button"]'
    ) as HTMLButtonElement;

    button.click();

    expect(cancelar)
      .toHaveBeenCalledExactlyOnceWith(venda.id);
  });

  it('should allow the seller to complete the sale', () => {
    const concluir = vi.fn();

    component.concluir.subscribe(concluir);

    const element = renderizar('VENDA');

    expect(element.textContent)
      .toContain('Steven Seagal');

    const button = element.querySelector(
      '[data-testid="complete-sale-button"]'
    ) as HTMLButtonElement;

    expect(button).not.toBeNull();

    button.click();

    expect(concluir)
      .toHaveBeenCalledExactlyOnceWith(venda.id);
  });

  it('should not display actions for a completed sale', () => {
    const vendaConcluida: VendaResponse = {
      ...venda,
      statusVenda: 'CONCLUIDA',
      veiculo: {
        ...venda.veiculo,
        status: 'VENDIDO',
      },
    };

    const element = renderizar(
      'VENDA',
      vendaConcluida
    );

    expect(element.textContent)
      .toContain('Concluída');

    expect(
      element.querySelector(
        '[data-testid="cancel-sale-button"]'
      )
    ).toBeNull();

    expect(
      element.querySelector(
        '[data-testid="complete-sale-button"]'
      )
    ).toBeNull();
  });
});