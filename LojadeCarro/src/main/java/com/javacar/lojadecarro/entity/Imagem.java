package com.javacar.lojadecarro.entity;

import com.javacar.lojadecarro.dto.response.UploadResult;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "Imagem")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Imagem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome_original", nullable = false, length = 255)
    private String nomeOriginal;

    @Column(name = "object_key", nullable = false, length = 500)
    private String objectKey;

    @Column(name = "bucket", nullable = false, length = 100)
    private String bucket;

    @Column(name = "content_type", nullable = false, length = 100)
    private String contentType;

    @Column(name = "tamanho", nullable = false)
    private Long tamanho;

    @Column(name = "principal", nullable = false)
    private boolean principal;

    @ManyToOne(fetch = FetchType.LAZY)
    private Veiculo veiculo;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false,
            insertable = false
    )
    private LocalDateTime dataCadastro;

    public Imagem(UploadResult upload) {
        this.nomeOriginal = upload.nomeOriginal();
        this.objectKey = upload.objectKey();
        this.bucket = upload.bucket();
        this.contentType = upload.contentType();
        this.tamanho = upload.tamanho();
        this.principal = false;
    }

}
