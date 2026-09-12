import { ComponentFixture, TestBed } from '@angular/core/testing';
import { VeiculoCard } from './veiculo-card';
import { provideRouter } from '@angular/router';
import { VeiculoResponse } from '../../models/veiculo-response';

describe('VeiculoCard', () => {
  let component: VeiculoCard;
  let fixture: ComponentFixture<VeiculoCard>;

  const veiculo: VeiculoResponse = {
    id: 1,
    placa: 'ABC1D23',
    marca: 'Chevrolet',
    modelo: 'Onix',
    carroceria: 'Hatch',
    cor: 'Branco',
    combustivel: 'Flex',
    valor: 75990,
    quilometragem: 70000,
    anoFabricacao: 2023,
    statusVeiculo: 'DISPONIVEL',
    imagemPrincipalId: 10,
    descricao: 'Veículo em ótimo estado',
  };


  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        provideRouter([]),
      ],
      imports: [VeiculoCard],
    }).compileComponents();

    fixture = TestBed.createComponent(VeiculoCard);
    component = fixture.componentInstance;

    fixture.componentRef.setInput('veiculo', veiculo);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  it('should display vehicle information', () => {
    const element = fixture.nativeElement as HTMLElement;

    expect(
      element.querySelector('[data-testid="vehicle-title"]')
        ?.textContent
    ).toContain('Onix');

    expect(element.textContent).toContain('Chevrolet');
    expect(element.textContent).toContain('2023');
    expect(element.textContent).toContain('70.000 km');
  });

  it('should display the main vehicle image', () => {
    const element = fixture.nativeElement as HTMLElement;
    const image = element.querySelector(
      '[data-testid="vehicle-image"]'
    );

    expect(image?.getAttribute('src'))
      .toContain('/imagens/10/conteudo');
  });

  it('should display a placeholder when there is no image', () => {
    fixture.componentRef.setInput('veiculo', {
      ...veiculo,
      imagemPrincipalId: null,
    });

    fixture.detectChanges();

    const element = fixture.nativeElement as HTMLElement;

    expect(
      element.querySelector('[data-testid="vehicle-placeholder"]')
    ).not.toBeNull();

    expect(element.textContent)
      .toContain('Imagem indisponível');
  });
  it('should display the placeholder when image loading fails', () => {
    const element = fixture.nativeElement as HTMLElement;

    const image = element.querySelector(
      '[data-testid="vehicle-image"]'
    ) as HTMLImageElement;

    image.dispatchEvent(new Event('error'));
    fixture.detectChanges();

    expect(
      element.querySelector('[data-testid="vehicle-image"]')
    ).toBeNull();

    expect(
      element.querySelector('[data-testid="vehicle-placeholder"]')
    ).not.toBeNull();
  });

  it('should navigate to vehicle details', () => {
    const element = fixture.nativeElement as HTMLElement;

    const link = element.querySelector(
      '[data-testid="vehicle-details-link"]'
    );

    expect(link?.getAttribute('href'))
      .toBe('/veiculos/1');
  });
});
