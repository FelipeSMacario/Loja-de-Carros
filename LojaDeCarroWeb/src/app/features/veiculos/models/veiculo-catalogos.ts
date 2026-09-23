export interface CatalogoItem {
  id: number;
  nome: string;
  ativo: boolean;
}

export interface ModeloCatalogo extends CatalogoItem {
  marca: CatalogoItem;
}

export interface VeiculoCatalogos {
  carrocerias: CatalogoItem[];
  combustiveis: CatalogoItem[];
  cores: CatalogoItem[];
  modelos: ModeloCatalogo[];
  opcionais: CatalogoItem[];
}