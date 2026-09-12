package com.javacar.lojadecarro.dto.request;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.multipart.MultipartFile;

@Schema(description = "Partes do cadastro de um veículo")
public record VeiculoCadastroMultipartOpenApi(

                                              VeiculoRequest request,

                                              @ArraySchema(
                                                      schema = @Schema(
                                                              type = "string",
                                                              format = "binary"
                                                      )
                                              )
                                              MultipartFile[] files
) {
}