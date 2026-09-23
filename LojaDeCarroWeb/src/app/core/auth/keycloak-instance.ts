import { InjectionToken } from '@angular/core';
import Keycloak from 'keycloak-js';

import { environment } from
  '../../../environments/environment';

export const KEYCLOAK_INSTANCE =
  new InjectionToken<Keycloak>(
    'KEYCLOAK_INSTANCE',
    {
      providedIn: 'root',
      factory: () =>
        new Keycloak({
          url: environment.keycloak.url,
          realm: environment.keycloak.realm,
          clientId: environment.keycloak.clientId,
        }),
    }
  );