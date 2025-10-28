package com.example.version1.model;

import jakarta.persistence.*;
import org.antlr.v4.runtime.misc.LogManager;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Ubicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @OneToMany(mappedBy = "ubicacion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Usuario> usuarios = new ArrayList<>();

    @OneToMany(mappedBy = "ubicacion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Local> locales;

    public Ubicacion() {
    }

    public Ubicacion(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    public List<Local> getLocales() {
        return locales;
    }

    public void setLocales(List<Local> locales) {
        this.locales = locales;
    }


    // helpers para mantener ambas caras de la relación
    public void addUsuario(Usuario u) {
        usuarios.add(u);
        u.setUbicacion(this);
    }

    public void removeUsuario(Usuario u) {
        usuarios.remove(u);
        u.setUbicacion(null);
    }


}
