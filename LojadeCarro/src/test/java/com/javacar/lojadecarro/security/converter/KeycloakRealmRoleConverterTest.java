package com.javacar.lojadecarro.security.converter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DisplayName("Testes do conversor de roles do Keycloak")
class KeycloakRealmRoleConverterTest {

    private final KeycloakRealmRoleConverter converter =
            new KeycloakRealmRoleConverter();

    @Test
    @DisplayName("Deve converter roles do realm em authorities")
    void deveConverterRolesDoRealmEmAuthorities() {
        var jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim(
                        "realm_access",
                        Map.of(
                                "roles",
                                List.of("ADMIN", "USUARIO")
                        )
                )
                .build();

        var resultado = converter.convert(jwt);

        assertThat(resultado)
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly(
                        "ROLE_ADMIN",
                        "ROLE_USUARIO"
                );
    }

    @Test
    @DisplayName("Deve retornar authorities vazias quando realm_access não existir")
    void deveRetornarAuthoritiesVaziasQuandoRealmAccessNaoExistir() {
        var jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("sub", "subject-keycloak")
                .build();

        var resultado = converter.convert(jwt);

        assertThat(resultado).isEmpty();
    }

}
