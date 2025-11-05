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
    private String tipo; // GENERAL, VIP
    private int cantidad_consumiciones;


    @ManyToOne(fetch = FetchType.LAZY)
    private Usuario usuario;


    @ManyToOne(fetch = FetchType.LAZY)
    private Evento evento;

    // CONSTRUCTORES
    public Entrada() {}

    public Entrada(String tipo, Usuario usuario, Evento evento, int cantidad_consumiciones) {
        this.nombre_evento = evento.getTitulo();
        this.nombre_usuario = usuario.getNombre();
        this.tipo = tipo;
        this.usuario = usuario;
        this.evento = evento;
        this.cantidad_consumiciones = cantidad_consumiciones;
    }

    // GETTERS Y SETTERS
    public Long getId() {
        return id;
    }

    public String getNombre_evento() {
        return nombre_evento;
    }

    public String getNombre_usuario() {
        return nombre_usuario;
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
    public int getCantidad_consumiciones() {
        return cantidad_consumiciones;
    }
    public void setCantidad_consumiciones(int cantidad_consumiciones) {
        this.cantidad_consumiciones = cantidad_consumiciones;
    }



}
