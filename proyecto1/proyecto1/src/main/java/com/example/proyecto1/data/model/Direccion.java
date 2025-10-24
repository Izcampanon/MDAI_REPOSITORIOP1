package com.example.proyecto1.data.model;


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

    public Direccion() {}

    public Direccion(String dir, String ciudad, Usuario usuario) {
        this.dir = dir;
        this.ciudad = ciudad;
        this.usuario = usuario;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
