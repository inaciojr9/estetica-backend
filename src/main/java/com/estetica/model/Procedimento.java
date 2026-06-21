package com.estetica.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "procedimento")
public class Procedimento extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String titulo;

    @Column(name = "texto_explicativo", columnDefinition = "TEXT")
    public String textoExplicativo;

    public String tipo; // "ROSTO" ou "INTIMO"

    @Column(name = "url_midia")
    public String urlMidia;

    @Column(name = "data_criacao")
    public LocalDateTime dataCriacao = LocalDateTime.now();
}
