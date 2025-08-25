package com.jugueteria.api.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data // Anotación de Lombok: genera getters, setters, toString, equals y hashCode.
@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 20, unique = true, nullable = false)
    private String nombre;
}