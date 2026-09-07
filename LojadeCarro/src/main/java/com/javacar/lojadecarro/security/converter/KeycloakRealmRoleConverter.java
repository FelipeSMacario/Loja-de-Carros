package com.javacar.lojadecarro.security.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.List;

public class KeycloakRealmRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    private static final String REALM_ACCESS = "realm_access";
    private static final String ROLES = "roles";
    private static final String ROLE_PREFIX = "ROLE_";

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        var realmAccess = jwt.getClaimAsMap(REALM_ACCESS);

        if (realmAccess == null) {
            return List.of();
        }

        var roles = realmAccess.get(ROLES);

        if (!(roles instanceof Collection<?> roleCollection)) {
            return List.of();
        }

        return roleCollection.stream()
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .filter(role -> !role.isBlank())
                .map(this::adicionarPrefixo)
                .map(this::criarAuthority)
                .toList();
    }

    private String adicionarPrefixo(String role) {
        return role.startsWith(ROLE_PREFIX)
                ? role
                : ROLE_PREFIX + role;
    }

    private GrantedAuthority criarAuthority(String role) {
        return new SimpleGrantedAuthority(role);
    }
}
