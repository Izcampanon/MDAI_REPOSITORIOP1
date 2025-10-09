package com.example.proyecto1;


import jakarta.persistence.*;

@Entity
public class Direccion {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    private String dir;
    private String ciudad;

    @ManyToOne
    private Usuario usuario;
}
