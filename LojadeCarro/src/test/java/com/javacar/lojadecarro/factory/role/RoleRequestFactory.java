package com.javacar.lojadecarro.factory.role;

import com.javacar.lojadecarro.dto.request.RoleRequest;

public class RoleRequestFactory {
    private Long id;

    private RoleRequestFactory() {
    }

    public static RoleRequestFactory roleRequestFactory() {
        return new RoleRequestFactory();
    }

    public static RoleRequestFactory criarRequest() {
        return new RoleRequestFactory();
    }

    public RoleRequestFactory comTodosOsCampos() {
        this.id = 1L;
        return this;
    }

    public RoleRequestFactory comId(Long id) {
        this.id = id;
        return this;
    }

    public RoleRequest build() {
        return new RoleRequest(id);
    }
}
