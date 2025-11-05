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
    private int edad;
    private String telefono;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Compra_Entrada> entardas_compradas = new ArrayList<>();

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "ubicacion_id")
    private Ubicacion ubicacion;

    public Usuario() {
    }

    public Usuario(String nombre, String email, String contrasenia, int edad, String telefono) {
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

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public List<Compra_Entrada> getEntardas_compradas() {
        return entardas_compradas;
    }

    public void setEntardas_compradas(List<Compra_Entrada> entardas_compradas) {
        this.entardas_compradas = entardas_compradas;
    }

    public Ubicacion getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(Ubicacion ubicacion) {
        this.ubicacion = ubicacion;
    }
}