package com.example.version1.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private LocalDateTime fecha;
    private String artista;
    private String ubicacion;
    private String descripcion;
    private int aforo;
    private String estado; //disponible proximo acabado
    private String tipo; //+18 -18
    private boolean estado_aforo; //completo incompleto

    // RELACIONES
    @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL)
    private List<Entrada> entradas = new ArrayList<>();

    @ManyToOne
    private Local local;


    // CONSTRUCTORES
    public Evento() {}

    public Evento(String titulo, LocalDateTime fecha, String artista, String ubicacion, String descripcion, int aforo, String estado, String tipo, boolean estado_aforo, Local local) {
        this.titulo = titulo;
        this.fecha = fecha;
        this.artista = artista;
        this.ubicacion = ubicacion;
        this.descripcion = descripcion;
        this.aforo = aforo;
        this.estado = estado;
        this.tipo = tipo;
        this.estado_aforo = estado_aforo;
        this.local = local;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getArtista() {
        return artista;
    }

    public void setArtista(String artista) {
        this.artista = artista;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getAforo() {
        return aforo;
    }

    public void setAforo(int aforo) {
        this.aforo = aforo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public boolean isEstado_aforo() {
        return estado_aforo;
    }

    public void setEstado_aforo(boolean estado_aforo) {
        this.estado_aforo = estado_aforo;
    }

    public List<Entrada> getEntradas() {
        return entradas;
    }

    public void setEntradas(List<Entrada> entradas) {
        this.entradas = entradas;
    }

    public Local getLocal() {
        return local;
    }

    public void setLocal(Local local) {
        this.local = local;
    }
}


