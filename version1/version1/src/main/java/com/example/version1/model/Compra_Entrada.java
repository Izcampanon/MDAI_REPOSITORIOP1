package com.example.version1.model;

import jakarta.persistence.*;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

@Entity
public class Compra_Entrada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date fechaCompra;
    private float precio;

    // Lista de entradas asociadas a esta compra: definir como relacion OneToMany con tabla intermedia
    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "compra_entrada_entardas",
            joinColumns = @JoinColumn(name = "compra_entrada_id"),
            inverseJoinColumns = @JoinColumn(name = "entrada_id"))
    private List<Entrada> tipo_entradas = new ArrayList<>();

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    public Compra_Entrada() {
        // Constructor por defecto necesario para JPA
    }

    public Compra_Entrada(Date fechaCompra, float precio, List<Entrada> tipo_entradas, Usuario usuario) {
        this.fechaCompra = fechaCompra;
        this.precio = precio;
        this.tipo_entradas = tipo_entradas;
        this.usuario = usuario;
    }

    public  Usuario getUsuario() {
        return usuario;
    }
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    public Long getId() {
        return id;
    }

    public Date getFechaCompra() {
        return fechaCompra;
    }
    public void setFechaCompra(Date fechaCompra) {
        this.fechaCompra = fechaCompra;
    }
    public float getPrecio() {
        return precio;
    }
    public void setPrecio(float precio) {
        this.precio = precio;
    }
    public List<Entrada> getTipo_entradas() {
        return tipo_entradas;
    }
    public void setTipo_entradas(List<Entrada> tipo_entradas) {
        this.tipo_entradas = tipo_entradas;
    }

}
