package com.example.version1.service;

import com.example.version1.model.Local;
import com.example.version1.model.Ubicacion;
import com.example.version1.model.Evento;

import java.util.List;
import java.util.Optional;

public interface AdminService {

    // Ubicaciones
    Ubicacion crearUbicacion(Ubicacion ubicacion);
    List<Ubicacion> listarUbicaciones();
    Optional<String> validarUbicacion(Ubicacion ubicacion);

    // Locales
    Local crearLocal(Local local, Long ubicacionId);
    List<Local> listarLocales();
    Optional<String> validarLocal(Local local, Long ubicacionId);

    // Eventos
    Evento crearEvento(Evento evento, Long localId);
    List<Evento> listarEventos();
    Optional<String> validarEvento(Evento evento, Long localId);

}
