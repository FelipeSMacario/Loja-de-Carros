package com.javacar.lojadecarro.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javacar.lojadecarro.security.WebSecurityConfig;
import com.javacar.lojadecarro.security.service.CustomUserDetailsService;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


@AutoConfigureMockMvc()
@Import(WebSecurityConfig.class)
public abstract class BaseControllerTest {
    @Autowired
    protected MockMvc mockMvc;
    @MockitoBean
    private CustomUserDetailsService userDetailsService;
    @MockitoBean
    private JwtDecoder jwtDecoder;
    @Autowired
    protected ObjectMapper objectMapper;

    @AfterEach
    void limparSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    protected ResultActions performGet(String url) throws Exception {
        return mockMvc.perform(get(url));
    }

    protected ResultActions performGetComAutenticacao(String url,
                                                      String jwtId,
                                                      String role) throws Exception {
        return mockMvc.perform(get(url).with(jwt()
                .jwt(jwt -> jwt.subject(jwtId))
                .authorities(
                        new SimpleGrantedAuthority(role)
                )
        ));
    }

    protected ResultActions performGetComAutenticacao(String url,
                                                      String parametro,
                                                      String valor,
                                                      String jwtId,
                                                      String role) throws Exception {
        return mockMvc.perform(get(url)
                .param(parametro, valor)
                .with(jwt()
                        .jwt(jwt -> jwt.subject(jwtId))
                        .authorities(
                                new SimpleGrantedAuthority(role)
                        )
                ));
    }

    protected ResultActions performGet(String url, String parametro, String valor) throws Exception {
        return mockMvc.perform(get(url).param(parametro, valor));
    }

    protected ResultActions performPost(String url, Object body) throws Exception {
        return mockMvc.perform(
                post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body))
        );
    }

    protected ResultActions performPostComAutenticacao(String url,
                                                       Object body,
                                                       String jwtId,
                                                       String role) throws Exception {
        return mockMvc.perform(
                post(url)
                        .with(jwt()
                                .jwt(jwt -> jwt.subject(jwtId))
                                .authorities(
                                        new SimpleGrantedAuthority(role)
                                )
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body))
        );
    }

    protected ResultActions performPost(String url,
                                        Object body,
                                        MockMultipartFile... files) throws Exception {

        var request = multipart(url);

        request.file(new MockMultipartFile(
                "request",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(body)
        ));

        for (MockMultipartFile file : files) {
            request.file(file);
        }

        return mockMvc.perform(request);
    }

    protected ResultActions performPostComAutenticacao(String url,
                                                       Object body,
                                                       String jwtId,
                                                       String role,
                                                       MockMultipartFile... files) throws Exception {

        var request = multipart(url);

        request.file(new MockMultipartFile(
                "request",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(body)
        )).with(jwt()
                .jwt(jwt -> jwt.subject(jwtId))
                .authorities(
                        new SimpleGrantedAuthority(role)
                )
        );

        for (MockMultipartFile file : files) {
            request.file(file);
        }

        return mockMvc.perform(request);
    }

    protected ResultActions performPut(String url, Object body) throws Exception {
        return mockMvc.perform(
                put(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body))
        );
    }

    protected ResultActions performPutComAutenticacao(String url,
                                                      Object body,
                                                      String jwtId,
                                                      String role) throws Exception {
        return mockMvc.perform(
                put(url)
                        .with(jwt()
                                .jwt(jwt -> jwt.subject(jwtId))
                                .authorities(
                                        new SimpleGrantedAuthority(role)
                                )
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(body)
                        )
        );

    }

    protected ResultActions performPatch(String url, Object body) throws Exception {
        return mockMvc.perform(
                patch(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body))
        );
    }

    protected ResultActions performPatchComAutenticacao(String url,
                                                        Object body,
                                                        String jwtId,
                                                        String role) throws Exception {
        return mockMvc.perform(
                patch(url)
                        .with(jwt()
                                .jwt(jwt -> jwt.subject(jwtId))
                                .authorities(
                                        new SimpleGrantedAuthority(role)
                                )
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body))
        );
    }

    protected ResultActions performPatchComAutenticacao(String url,
                                                        String jwtId,
                                                        String role) throws Exception {
        return mockMvc.perform(
                patch(url)
                        .with(jwt()
                                .jwt(jwt -> jwt.subject(jwtId))
                                .authorities(
                                        new SimpleGrantedAuthority(role)
                                )
                        )
        );
    }

    protected ResultActions performPatch(String url) throws Exception {
        return mockMvc.perform(
                patch(url)
        );
    }

    protected ResultActions performDelete(String url) throws Exception {
        return mockMvc.perform(delete(url));
    }

    protected ResultActions performDelete(String url, String param, List<Long> parametros) throws Exception {

        var request = delete(url);

        parametros.forEach(id ->
                request.param(param, id.toString())
        );

        return mockMvc.perform(request);
    }

    protected ResultActions performDeleteComAutenticacao(String url, String jwtId, String role) throws Exception {

        return mockMvc.perform(
                delete(url)
                        .with(jwt()
                                .jwt(jwt -> jwt.subject(jwtId))
                                .authorities(
                                        new SimpleGrantedAuthority(role)
                                )
                        )
        );

    }

    protected ResultActions performDeleteComAutenticacao(String url,
                                                         String param,
                                                         List<Long> parametros,
                                                         String jwtId,
                                                         String role) throws Exception {

        var request = delete(url)
                .with(jwt()
                        .jwt(jwt -> jwt.subject(jwtId))
                        .authorities(
                                new SimpleGrantedAuthority(role)
                        )
                );

        parametros.forEach(id ->
                request.param(param, id.toString())
        );

        return mockMvc.perform(request);
    }
}
