package com.example.version1.service;

import com.example.version1.model.Compra_Entrada;
import com.example.version1.model.Entrada;
import com.example.version1.model.Evento;
import com.example.version1.model.Local;
import com.example.version1.model.Usuario;
import com.example.version1.repository.RepositoryCompra_Entrada;
import com.example.version1.repository.RepositoryEntrada;
import com.example.version1.repository.RepositoryEvento;
import com.example.version1.repository.RepositoryLocal;
import com.example.version1.repository.RepositoryUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
public class CompraEntradaServiceImpl implements CompraEntradaService {

    private final RepositoryLocal repositoryLocal;
    private final RepositoryEvento repositoryEvento;
    private final RepositoryEntrada repositoryEntrada;
    private final RepositoryCompra_Entrada repositoryCompraEntrada;
    private final RepositoryUsuario repositoryUsuario;

    @Autowired
    public CompraEntradaServiceImpl(RepositoryLocal repositoryLocal, RepositoryEvento repositoryEvento, RepositoryEntrada repositoryEntrada, RepositoryCompra_Entrada repositoryCompraEntrada, RepositoryUsuario repositoryUsuario) {
        this.repositoryLocal = repositoryLocal;
        this.repositoryEvento = repositoryEvento;
        this.repositoryEntrada = repositoryEntrada;
        this.repositoryCompraEntrada = repositoryCompraEntrada;
        this.repositoryUsuario = repositoryUsuario;
    }

    @Override
    public List<Local> buscarLocalesPorUbicacion(String nombreUbicacion) {
        if (nombreUbicacion == null || nombreUbicacion.trim().isEmpty()) {
            return List.of();
        }
        return repositoryLocal.findByNombreUbicacion(nombreUbicacion.trim());
    }

    @Override
    @Transactional
    public Local obtenerLocalConEventos(Long localId) {
        if (localId == null) return null;
        // Obtener el local con todos sus eventos y filtrar los eventos que estén disponibles y no pasados
        Local local = repositoryLocal.findByIdConEventos(localId);
        if (local == null) return null;
        try {
            List<Evento> evs = local.getEventos();
            if (evs != null) {
                LocalDateTime ahora = LocalDateTime.now();
                List<Evento> filtrados = evs.stream()
                        .filter(ev -> ev != null
                                && Boolean.TRUE.equals(ev.getEstado())
                                && ev.getFecha() != null
                                && (ev.getFecha().isAfter(ahora) || ev.getFecha().isEqual(ahora)))
                        .collect(Collectors.toList());

                // Para cada evento calculamos las plazas restantes usando repositoryEntrada
                for (Evento ev : filtrados) {
                    try {
                        Long eventoId = ev.getId();
                        if (eventoId != null) {
                            long ocupadas = repositoryEntrada.countByEventoId(eventoId);
                            int aforo = ev.getAforo();
                            int restantes = (aforo >= 0) ? Math.max(aforo - (int) ocupadas, 0) : Integer.MAX_VALUE;
                            // asignar plazas restantes
                            ev.setPlazasRestantes((aforo >= 0) ? restantes : null);

                            // si ya no quedan plazas, marcar como no disponible
                            if (aforo >= 0 && ocupadas >= aforo) {
                                ev.setEstado(false);
                            }
                        }
                    } catch (Exception ex) {
                        // ignorar errores de conteo y continuar
                    }
                }

                local.setEventos(filtrados);
            }
        } catch (Exception e) {
            System.out.println("Error filtrando eventos del local: " + e.getMessage());
        }
        return local;
    }

    @Override
    public Evento obtenerEvento(Long eventoId) {
        if (eventoId == null) return null;
        Optional<Evento> opt = repositoryEvento.findById(eventoId);
        return opt.orElse(null);
    }

    @Override
    public Optional<String> validarEntrada(String tipo, int cantidadConsumiciones, Evento evento, Usuario usuario) {
        if (evento == null) return Optional.of("Evento no encontrado");
        // estado
        if (evento.getEstado() != null && !evento.getEstado()) return Optional.of("Evento no disponible");
        // fecha: evento pasado
        try {
            if (evento.getFecha() == null) return Optional.of("Evento sin fecha");
            java.time.LocalDateTime ahora = java.time.LocalDateTime.now();
            if (evento.getFecha().isBefore(ahora)) return Optional.of("Evento ya ha pasado");
        } catch (Exception e) {
            // si hay problemas con fecha, considerarlo no válido
            return Optional.of("Evento no válido");
        }
        // cantidad
        if (cantidadConsumiciones <= 0) return Optional.of("La cantidad de consumiciones debe ser mayor que 0");
        if(cantidadConsumiciones >20) return Optional.of("La cantidad de consumiciones no puede ser mayor que 10");
        // aforo (contamos entradas ya registradas)
        long ocupadas = repositoryEntrada.countByEventoId(evento.getId());
        if (evento.getAforo() >= 0 && ocupadas >= evento.getAforo()) return Optional.of("El evento está lleno");
        // edad
        if (evento.getEdadpermitida() > 0) {
            if (usuario == null) return Optional.of("Debes iniciar sesión para comprar esta entrada");
            if (usuario.getEdad() < evento.getEdadpermitida()) return Optional.of("No cumples la edad mínima para este evento");
        }
        // tipo válido
        if (tipo == null) return Optional.of("Tipo de entrada no válido");
        if (!"GENERAL".equalsIgnoreCase(tipo) && !"VIP".equalsIgnoreCase(tipo)) return Optional.of("Tipo de entrada no válido");
        return Optional.empty();
    }

    @Override
    public Optional<String> procesarPago(Evento evento, String tipo, int cantidadConsumiciones, com.example.version1.model.Usuario usuario) {
        // Calcular precio usando los precios del evento
        if (evento == null) return Optional.of("Evento no encontrado");
        float precioUnitario = "VIP".equalsIgnoreCase(tipo) ? evento.getPrecioVip() : evento.getPrecioGeneral();
        float precioTotal = precioUnitario + (cantidadConsumiciones * evento.getPrecioConsumicion());

        // comprobar saldo
        if (usuario == null) return Optional.of("Usuario no autenticado");
        if (usuario.getSaldo() < precioTotal) return Optional.of("Saldo insuficiente para realizar la compra");

        try {
            // Crear entrada
            Entrada entrada = new Entrada(tipo.toUpperCase(), usuario, evento, cantidadConsumiciones);

            // Asegurar que los campos de texto están poblados para persistencia clara
            if (entrada.getNombre_evento() == null || entrada.getNombre_evento().isEmpty()) {
                entrada.setNombre_evento(evento.getTitulo());
            }
            if (entrada.getNombre_usuario() == null || entrada.getNombre_usuario().isEmpty()) {
                entrada.setNombre_usuario(usuario.getNombre());
            }

            // Crear compra
            List<Entrada> lista = new ArrayList<>();
            lista.add(entrada);
            Compra_Entrada compra = new Compra_Entrada(new Date(), precioTotal, lista, usuario);

            // Persistir compra (cascade guardará la entrada)
            // Usar transacción para asegurar consistencia
            saveCompraYActualizarUsuario(compra, usuario, precioTotal);

            System.out.println("[CompraEntradaService] Compra realizada. Usuario: " + usuario.getEmail() + " - Precio: " + precioTotal);
            return Optional.empty();
        } catch (Exception e) {
            System.out.println("Error al procesar la compra: " + e.getMessage());
            return Optional.of("Error al procesar la compra: " + e.getMessage());
        }
    }

    @Transactional
    protected void saveCompraYActualizarUsuario(Compra_Entrada compra, Usuario usuario, float precioTotal) {
        // Guardar la compra; CascadeType.ALL en Compra_Entrada garantizará que las entradas se guarden
        repositoryCompraEntrada.save(compra);

        // Añadir la compra a la lista de compras del usuario para mantener la relación en memoria
        try {
            if (usuario.getEntardas_compradas() == null) usuario.setEntardas_compradas(new ArrayList<>());
            usuario.getEntardas_compradas().add(compra);
        } catch (Exception ex) {
            // Ignorar problemas al actualizar la lista en memoria
        }

        // Descontar saldo y guardar usuario
        usuario.setSaldo(usuario.getSaldo() - precioTotal);
        repositoryUsuario.save(usuario);
    }

    // java
    @Transactional
    public Optional<String> devolverCompra(Long compraId, Usuario usuario) {
        if (compraId == null) return Optional.of("Id de compra requerido");
        if (usuario == null) return Optional.of("Usuario no autenticado");

        Optional<Compra_Entrada> optCompra = repositoryCompraEntrada.findById(compraId);
        if (optCompra.isEmpty()) return Optional.of("Compra no encontrada");
        Compra_Entrada compra = optCompra.get();

        // Verificar propiedad
        try {
            Usuario propietario = compra.getUsuario();
            if (propietario == null || propietario.getId() == null || !propietario.getId().equals(usuario.getId())) {
                return Optional.of("No autorizado para devolver esta compra");
            }
        } catch (Exception e) {
            return Optional.of("No se pudo verificar la propiedad de la compra");
        }

        // Comprobar si ya fue devuelta (intentar varios getters)
        try {
            Boolean yaDevuelta = null;
            try {
                yaDevuelta = (Boolean) compra.getClass().getMethod("isDevuelta").invoke(compra);
            } catch (NoSuchMethodException ex1) {
                try { yaDevuelta = (Boolean) compra.getClass().getMethod("getDevuelta").invoke(compra); } catch (Exception ex2) { }
            }
            if (Boolean.TRUE.equals(yaDevuelta)) return Optional.of("La compra ya fue devuelta");
        } catch (Exception ignore) { }

        // Obtener importe pagado (intenta varios getters comunes)
        float monto = 0f;
        try {
            String[] posibles = new String[] { "getPrecio", "getPrecioTotal", "getPrecio_total", "getImporte", "getAmount" };
            for (String mname : posibles) {
                try {
                    Object val = compra.getClass().getMethod(mname).invoke(compra);
                    if (val instanceof Number) { monto = ((Number) val).floatValue(); break; }
                } catch (NoSuchMethodException ignored) { }
            }
        } catch (Exception e) {
            return Optional.of("No se pudo determinar el importe de la compra");
        }

        if (monto <= 0f) return Optional.of("Importe de la compra no válido para devolución");

        // Realizar devolución: recargar saldo y marcar compra como devuelta
        try {
            usuario.setSaldo(usuario.getSaldo() + monto);
            repositoryUsuario.save(usuario);

            // Marcar la compra como devuelta si existe setter
            try {
                try {
                    compra.getClass().getMethod("setDevuelta", boolean.class).invoke(compra, true);
                } catch (NoSuchMethodException ex1) {
                    try { compra.getClass().getMethod("setDevuelto", boolean.class).invoke(compra, true); } catch (NoSuchMethodException ex2) { }
                }
            } catch (Exception ignore) { }

            repositoryCompraEntrada.save(compra); // persistir cambio de estado si aplica

            System.out.println("[CompraEntradaService] Devolución realizada. Usuario: " + usuario.getEmail() + " - Monto: " + monto);
            return Optional.empty();
        } catch (Exception e) {
            return Optional.of("Error al procesar la devolución: " + e.getMessage());
        }
    }


}
