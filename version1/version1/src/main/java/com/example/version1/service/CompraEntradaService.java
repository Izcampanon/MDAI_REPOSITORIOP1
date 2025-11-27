package com.example.version1.service;

import com.example.version1.model.Evento;
import com.example.version1.model.Local;

import java.util.List;

public interface CompraEntradaService {
    List<Local> buscarLocalesPorUbicacion(String nombreUbicacion);

    // Obtener un Local con sus eventos cargados
    Local obtenerLocalConEventos(Long localId);

    // Obtener un Evento por id
    Evento obtenerEvento(Long eventoId);

    // Validar si una entrada es válida para un evento (aforo, estado, edad, etc.)
    java.util.Optional<String> validarEntrada(String tipo, int cantidadConsumiciones, Evento evento, com.example.version1.model.Usuario usuario);

    // Procesar el pago: comprueba saldo, descuenta, crea Entrada y Compra_Entrada; devuelve Optional<String> con error si falla
    java.util.Optional<String> procesarPago(Evento evento, String tipo, int cantidadConsumiciones, com.example.version1.model.Usuario usuario);
}
