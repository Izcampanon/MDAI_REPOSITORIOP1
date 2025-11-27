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
    private String direccion;
    private String descripcion;
    private int aforo;
    private Boolean estado; //disponible (true) /no disponible(false)
    private int edadpermitida; //+18 -18

    // Precios: precio por tipo y precio por consumición
    private float precioGeneral = 50.0f;
    private float precioVip = 80.0f;
    private float precioConsumicion = 5.0f;


    // RELACIONES
    @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL)
    private List<Entrada> entradas = new ArrayList<>();

    @ManyToOne
    private Local local;

    // Campo transitorio para UI: plazas restantes calculadas en servicio
    @Transient
    private Integer plazasRestantes;


    // CONSTRUCTORES
    public Evento() {}

    public Evento(String titulo, LocalDateTime fecha, String artista, String direccion, String descripcion, int aforo, Boolean estado, int edadpermitida, Local local) {
        this.titulo = titulo;
        this.fecha = fecha;
        this.artista = artista;
        this.direccion = direccion;
        this.descripcion = descripcion;
        this.aforo = aforo;
        this.estado = estado;
        this.edadpermitida = edadpermitida;
        this.local = local;
    }

    // GETTERS Y SETTERS
    public Long getId() {
        return id;
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

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
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

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public int getEdadpermitda() {
        return edadpermitida;
    }

    public void setEdadpermitida(int edadpermitida) {
        this.edadpermitida = edadpermitida;
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

    public int getEdadpermitida(){
        return edadpermitida;
    }

    // Precios getters/setters
    public float getPrecioGeneral() { return precioGeneral; }
    public void setPrecioGeneral(float precioGeneral) { this.precioGeneral = precioGeneral; }

    public float getPrecioVip() { return precioVip; }
    public void setPrecioVip(float precioVip) { this.precioVip = precioVip; }

    public float getPrecioConsumicion() { return precioConsumicion; }
    public void setPrecioConsumicion(float precioConsumicion) { this.precioConsumicion = precioConsumicion; }

    public boolean estadoDiponible(Usuario usuario) {
        boolean disponible = true;

        java.time.LocalDateTime ahora = java.time.LocalDateTime.now();

        // 1) Fecha: disponible si la fecha NO ha pasado (futura o igual a ahora)
        if (this.getFecha() == null) {
            disponible = false;
        } else if (this.getFecha().isBefore(ahora)) { // si ya pasó -> no disponible
            disponible = false;
        }

        // 2) Aforo: si entradas >= aforo -> no disponible
        int entradasCount = (this.getEntradas() == null) ? 0 : this.getEntradas().size();
        int aforo = this.getAforo(); // primitivo int
        if (aforo >= 0 && entradasCount >= aforo) {
            disponible = false;
        }

        // 3) Edad mínima: usar el getter existente (getEdadpermitda)
        int edadMinima = this.getEdadpermitda(); // primitivo int
        if (edadMinima > 0) {
            if (usuario == null) {
                disponible = false;
            } else {
                if (usuario.getEdad() < edadMinima) {
                    disponible = false;
                }
            }
        }

        // actualizar campo estado para que los tests que filtran por él funcionen
        this.setEstado(Boolean.valueOf(disponible));
        return disponible;
    }

    // Campo transitorio: plazas restantes
    public Integer getPlazasRestantes() {
        return plazasRestantes;
    }

    public void setPlazasRestantes(Integer plazasRestantes) {
        this.plazasRestantes = plazasRestantes;
    }

}
