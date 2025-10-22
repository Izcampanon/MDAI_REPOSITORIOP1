package com.example.version1.model;

import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
public class Compra_Entrada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date fechaCompra;
    private float precio;
    private List<Entrada> tipo_entradas;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "entrada_id")
    private Entrada Entrada;

    public Compra_Entrada() {
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

    public Entrada getEntrada() {
        return Entrada;
    }

    public void setEntrada(Entrada entrada) {
        Entrada = entrada;
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
