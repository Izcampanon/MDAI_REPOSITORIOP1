package com.example.version1.model;

import jakarta.persistence.*;

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

    @OneToMany(mappedBy = "ubicacion", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Local> locales = new ArrayList<>();

    public Ubicacion() {
    }

    public Ubicacion(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public List<Usuario> getUsuarios() { return usuarios; }

    // helpers para mantener ambas caras de la relación
    public void addUsuario(Usuario u) {
        usuarios.add(u);
        u.setUbicacion(this);
    }

    public void removeUsuario(Usuario u) {
        usuarios.remove(u);
        u.setUbicacion(null);
    }

    // getters/setters y helpers para locales
    public List<Local> getLocales() { return locales; }

    public void setLocales(List<Local> locales) { this.locales = locales; }

    public void addLocal(Local l) {
        locales.add(l);
        l.setUbicacion(this);
    }

    public void removeLocal(Local l) {
        locales.remove(l);
        l.setUbicacion(null);
    }


}
