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

    // Indica si la entrada ha sido devuelta
    private boolean devuelta = false;


    @ManyToOne(fetch = FetchType.LAZY)
    private Usuario usuario;


    @ManyToOne(fetch = FetchType.LAZY)
    private Evento evento;

    // CONSTRUCTORES
    public Entrada() {}

    public Entrada(String tipo, Usuario usuario, Evento evento, int cantidad_consumiciones) {
        // Evitar NullPointerException si se pasa usuario o evento nulo
        this.nombre_evento = (evento != null) ? evento.getTitulo() : null;
        this.nombre_usuario = (usuario != null) ? usuario.getNombre() : null;
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

    // añadido: permitir establecer nombre_evento desde el servicio
    public void setNombre_evento(String nombre_evento) {
        this.nombre_evento = nombre_evento;
    }

    public String getNombre_usuario() {
        return nombre_usuario;
    }

    public void setNombre_usuario(String nombre_usuario) { this.nombre_usuario = nombre_usuario; }

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

    public boolean isDevuelta() { return devuelta; }
    public void setDevuelta(boolean devuelta) { this.devuelta = devuelta; }


}
