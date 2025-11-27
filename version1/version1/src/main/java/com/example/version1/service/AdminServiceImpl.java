package com.example.version1.service;

import com.example.version1.model.Evento;
import com.example.version1.model.Local;
import com.example.version1.model.Ubicacion;
import com.example.version1.repository.RepositoryEvento;
import com.example.version1.repository.RepositoryLocal;
import com.example.version1.repository.RepositoryUbicacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AdminServiceImpl implements AdminService {

    private final RepositoryUbicacion repositoryUbicacion;
    private final RepositoryLocal repositoryLocal;
    private final RepositoryEvento repositoryEvento;

    @Autowired
    public AdminServiceImpl(RepositoryUbicacion repositoryUbicacion, RepositoryLocal repositoryLocal, RepositoryEvento repositoryEvento) {
        this.repositoryUbicacion = repositoryUbicacion;
        this.repositoryLocal = repositoryLocal;
        this.repositoryEvento = repositoryEvento;
    }

    // Ubicaciones
    @Override
    @Transactional
    public Ubicacion crearUbicacion(Ubicacion ubicacion) {
        return repositoryUbicacion.save(ubicacion);
    }

    @Override
    public List<Ubicacion> listarUbicaciones() {
        return repositoryUbicacion.findAll();
    }

    @Override
    public Optional<String> validarUbicacion(Ubicacion ubicacion) {
        if (ubicacion == null) return Optional.of("Ubicación nula");
        String nombre = ubicacion.getNombre();
        if (nombre == null || nombre.trim().isEmpty()) return Optional.of("El nombre de la ubicación no puede estar vacío");
        if (nombre.trim().length() < 3) return Optional.of("El nombre debe tener al menos 3 caracteres");
        // comprobar existencia por nombre (ignorar mayúsculas)
        boolean existe = repositoryUbicacion.existsByNombreIgnoreCase(nombre.trim());
        if (existe) return Optional.of("Ya existe una ubicación con ese nombre");
        return Optional.empty();
    }

    // Locales
    @Override
    @Transactional
    public Local crearLocal(Local local, Long ubicacionId) {
        if (ubicacionId != null) {
            Ubicacion u = repositoryUbicacion.findById(ubicacionId).orElse(null);
            if (u != null) {
                local.setUbicacion(u);
            }
        }
        return repositoryLocal.save(local);
    }

    @Override
    public List<Local> listarLocales() {
        return repositoryLocal.findAll();
    }

    @Override
    public Optional<String> validarLocal(Local local, Long ubicacionId) {
        if (local == null) return Optional.of("Local nulo");
        String nombre = local.getNombre();
        if (nombre == null || nombre.trim().isEmpty()) return Optional.of("El nombre del local no puede estar vacío");
        if (nombre.trim().length() < 2) return Optional.of("El nombre del local debe tener al menos 2 caracteres");
        // si se indicó ubicacionId, comprobar que existe
        if (ubicacionId != null && !repositoryUbicacion.existsById(ubicacionId)) {
            return Optional.of("La ubicación seleccionada no existe");
        }
        // comprobar si ya existe un local con el mismo nombre (globalmente)
        Optional<Local> opt = repositoryLocal.findByNombreIgnoreCaseNative(nombre.trim());
        if (opt.isPresent()) return Optional.of("Ya existe un local con ese nombre");
        return Optional.empty();
    }

    // Eventos
    @Override
    @Transactional
    public Evento crearEvento(Evento evento, Long localId) {
        if (localId != null) {
            Local l = repositoryLocal.findById(localId).orElse(null);
            if (l != null) {
                evento.setLocal(l);
            }
        }
        return repositoryEvento.save(evento);
    }

    @Override
    public List<Evento> listarEventos() {
        return repositoryEvento.findAll();
    }

    @Override
    public Optional<String> validarEvento(Evento evento, Long localId) {
        if (evento == null) return Optional.of("Evento nulo");
        if (evento.getTitulo() == null || evento.getTitulo().trim().isEmpty()) return Optional.of("El título no puede estar vacío");
        if (evento.getFecha() == null) return Optional.of("La fecha es obligatoria");
        if (evento.getFecha().isBefore(LocalDateTime.now())) return Optional.of("La fecha debe ser futura");
        if (evento.getAforo() < 0) return Optional.of("El aforo no puede ser negativo");
        if (evento.getEdadpermitida() < 0) return Optional.of("La edad mínima no puede ser negativa");
        // Precios: validar que sean no negativos
        if (evento.getPrecioGeneral() < 0f) return Optional.of("El precio general no puede ser negativo");
        if (evento.getPrecioVip() < 0f) return Optional.of("El precio VIP no puede ser negativo");
        if (evento.getPrecioConsumicion() < 0f) return Optional.of("El precio por consumición no puede ser negativo");
        if (localId != null && !repositoryLocal.existsById(localId)) return Optional.of("El local seleccionado no existe");
        // opcional: verificar título único en el mismo local
        return Optional.empty();
    }
}
