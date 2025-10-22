package com.example.version1.model;


import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String email;
    private String contrasenia;
    private boolean edad;
    private String telefono;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Compra_Entrada> entardas_compradas = new ArrayList<>();

    @ManyToOne
    private Ubicacion ubicacion;

    public Usuario() {
    }

    public Usuario(String nombre, String email, String contrasenia, boolean edad, String telefono) {
        this.nombre = nombre;
        this.email = email;
        this.contrasenia = contrasenia;
        this.edad = edad;
        this.telefono = telefono;
    }

    // getters/setters
    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    public boolean isEdad() {
        return edad;
    }

    public void setEdad(boolean edad) {
        this.edad = edad;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

}