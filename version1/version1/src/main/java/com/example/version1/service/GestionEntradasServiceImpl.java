package com.example.version1.service;

import com.example.version1.model.Compra_Entrada;
import com.example.version1.model.Entrada;
import com.example.version1.repository.RepositoryCompra_Entrada;
import com.example.version1.repository.RepositoryEntrada;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class GestionEntradasServiceImpl implements GestionEntradasService {

    private final RepositoryEntrada repositoryEntrada;
    private final RepositoryCompra_Entrada repositoryCompraEntrada;
    private final com.example.version1.repository.RepositoryUsuario repositoryUsuario;

    @Autowired
    public GestionEntradasServiceImpl(RepositoryEntrada repositoryEntrada, RepositoryCompra_Entrada repositoryCompraEntrada, com.example.version1.repository.RepositoryUsuario repositoryUsuario) {
        this.repositoryEntrada = repositoryEntrada;
        this.repositoryCompraEntrada = repositoryCompraEntrada;
        this.repositoryUsuario = repositoryUsuario;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Entrada> obtenerEntradasPorUsuario(Long usuarioId) {
        if (usuarioId == null) return Collections.emptyList();
        try {
            List<Entrada> entradas = repositoryEntrada.findByUsuarioId(usuarioId);
            System.out.println("[GestionEntradasService] entradas directas encontradas: " + (entradas == null ? 0 : entradas.size()));

            List<Entrada> resultado = new ArrayList<>();
            if (entradas != null && !entradas.isEmpty()) {
                resultado.addAll(entradas);
            } else {
                // Fallback: buscar compras del usuario y reunir las entradas asociadas
                List<Compra_Entrada> compras = repositoryCompraEntrada.findByUsuarioId(usuarioId);
                System.out.println("[GestionEntradasService] compras encontradas: " + (compras == null ? 0 : compras.size()));
                if (compras != null && !compras.isEmpty()) {
                    for (Compra_Entrada c : compras) {
                        if (c.getTipo_entradas() != null) resultado.addAll(c.getTipo_entradas());
                    }
                    System.out.println("[GestionEntradasService] entradas agregadas desde compras: " + resultado.size());
                }
            }

            // Asegurar que nombre_evento y nombre_usuario están poblados para evitar problemas de lazy loading en la vista
            for (Entrada e : resultado) {
                try {
                    if ((e.getNombre_evento() == null || e.getNombre_evento().isEmpty()) && e.getEvento() != null) {
                        // acceder a e.getEvento().getTitulo() dentro de la transacción para forzar carga
                        String titulo = e.getEvento().getTitulo();
                        if (titulo != null) e.setNombre_evento(titulo);
                    }
                    if ((e.getNombre_usuario() == null || e.getNombre_usuario().isEmpty()) && e.getUsuario() != null) {
                        String nom = e.getUsuario().getNombre();
                        if (nom != null) e.setNombre_usuario(nom);
                    }
                } catch (Exception ex) {
                    // Ignorar, mantenemos la entrada tal cual
                }
            }

            return resultado;

        } catch (Exception e) {
            System.out.println("Error al obtener entradas por usuario: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public Optional<Entrada> obtenerEntradaPorId(Long id) {
        if (id == null) return Optional.empty();
        try {
            return repositoryEntrada.findById(id);
        } catch (Exception e) {
            System.out.println("Error al obtener entrada por id: " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<Compra_Entrada> obtenerCompraPorEntradaId(Long entradaId) {
        if (entradaId == null) return Optional.empty();
        try {
            Compra_Entrada c = repositoryCompraEntrada.findByEntradaId(entradaId);
            return Optional.ofNullable(c);
        } catch (Exception e) {
            System.out.println("Error al obtener compra por entrada id: " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public Optional<String> procesarDevolucion(Long entradaId, Long requesterUsuarioId) {
        if (entradaId == null) return Optional.of("Entrada no especificada");
        try {
            Optional<Entrada> optE = repositoryEntrada.findById(entradaId);
            if (optE.isEmpty()) return Optional.of("Entrada no encontrada");
            Entrada e = optE.get();
            if (e.isDevuelta()) return Optional.of("La entrada ya ha sido devuelta");

            if (e.getUsuario() == null || e.getUsuario().getId() == null) return Optional.of("No se puede determinar el propietario de la entrada");
            if (!e.getUsuario().getId().equals(requesterUsuarioId)) return Optional.of("No tienes permiso para devolver esta entrada");

            Compra_Entrada compra = repositoryCompraEntrada.findByEntradaId(entradaId);
            if (compra == null) return Optional.of("Compra asociada no encontrada");

            // Calcular reembolso proporcional por entrada dentro de la compra
            List<Entrada> entradasCompra = compra.getTipo_entradas();
            float refund = 0f;
            if (entradasCompra == null || entradasCompra.isEmpty()) {
                // fallback: devolver todo el precio
                refund = compra.getPrecio();
            } else {
                int count = entradasCompra.size();
                if (count <= 0) count = 1;
                refund = compra.getPrecio() / (float) count;
            }

            // Marcar entrada como devuelta
            e.setDevuelta(true);
            repositoryEntrada.save(e);

            // Ajustar precio de la compra (opcional)
            compra.setPrecio(compra.getPrecio() - refund);
            repositoryCompraEntrada.save(compra);

            // Reembolsar al usuario y persistir
            var user = e.getUsuario();
            user.setSaldo(user.getSaldo() + refund);
            repositoryUsuario.save(user);

            return Optional.empty();
        } catch (Exception ex) {
            System.out.println("Error procesando devolución: " + ex.getMessage());
            return Optional.of("Error al procesar la devolución: " + ex.getMessage());
        }
    }

}
