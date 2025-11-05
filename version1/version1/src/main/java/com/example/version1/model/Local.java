package com.example.version1.model;


import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Local {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @ManyToOne (fetch = FetchType.LAZY)
    private Ubicacion ubicacion;

    @OneToMany (mappedBy = "local", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Evento> eventos = new ArrayList<>();


    public Local() {
    }

    public Local(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public Local(Long id, String nombre, Ubicacion ubicacion) {
        this.id = id;
        this.nombre = nombre;
        this.ubicacion = ubicacion;
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

    public Ubicacion getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(Ubicacion ubicacion) {
        this.ubicacion = ubicacion;
    }

    public List<Evento> getEventos() {
        return eventos;
    }

    public void setEventos(List<Evento> eventos) {
        this.eventos = eventos;
    }

    // helpers para mantener la relación bidireccional con Evento
    public void addEvento(Evento e) {
        eventos.add(e);
        e.setLocal(this);
    }

    public void removeEvento(Evento e) {
        eventos.remove(e);
        e.setLocal(null);
    }

    //Solo se añadiran los eventos que esten disponibles
    public List<Evento> getEventosDisponibles() {
        List<Evento> disponibles = new ArrayList<>();
        for (Evento e : eventos) {
            // Primero, si el campo estado está marcado true, lo añadimos
            if (Boolean.TRUE.equals(e.getEstado())) {
                disponibles.add(e);
                continue;
            }
            // Si el campo estado es null o false, pero no confiamos sólo en el campo
            // podemos evaluar la disponibilidad en tiempo real (sin usuario -> solo fecha/aforo)
            try {
                if (e.getEstado() == null && e.estadoDiponible(null)) {
                    disponibles.add(e);
                }
            } catch (Exception ex) {
                // Ignorar si por alguna razón la evaluación falla
            }
        }
        return disponibles;
    }
}
