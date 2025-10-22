package com.example.version1.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
public class Entrada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre_evento;
    private String nombre_usuario;
    private boolean consumiciones; //TRUE (incluye consumicion) FALSE (no incluye consumicion)
    private String tipo; // GENERAL, VIP


    @ManyToOne(fetch = FetchType.LAZY)
    private Usuario usuario;


    @ManyToOne(fetch = FetchType.LAZY)
    private Evento evento;

    // CONSTRUCTORES
    public Entrada() {}

    public Entrada(String nombre_evento, String nombre_usuario, boolean consumiciones, String tipo, Usuario usuario, Evento evento) {
        this.nombre_evento = nombre_evento;
        this.nombre_usuario = nombre_usuario;
        this.consumiciones = consumiciones;
        this.tipo = tipo;
        this.usuario = usuario;
        this.evento = evento;
    }

    // GETTERS Y SETTERS
    public Long getId() {
        return id;
    }

    public String getNombre_evento() {
        return nombre_evento;
    }

    public void setNombre_evento(String nombre_evento) {
        this.nombre_evento = nombre_evento;
    }

    public String getNombre_usuario() {
        return nombre_usuario;
    }

    public void setNombre_usuario(String nombre_usuario) {
        this.nombre_usuario = nombre_usuario;
    }

    public boolean isConsumiciones() {
        return consumiciones;
    }

    public void setConsumiciones(boolean consumiciones) {
        this.consumiciones = consumiciones;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }
}
