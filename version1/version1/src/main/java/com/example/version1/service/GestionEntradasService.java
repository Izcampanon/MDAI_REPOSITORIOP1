package com.example.version1.service;

import com.example.version1.model.Compra_Entrada;
import com.example.version1.model.Entrada;

import java.util.List;
import java.util.Optional;

public interface GestionEntradasService {
    List<Entrada> obtenerEntradasPorUsuario(Long usuarioId);

    Optional<Entrada> obtenerEntradaPorId(Long id);

    Optional<Compra_Entrada> obtenerCompraPorEntradaId(Long entradaId);

    Optional<String> procesarDevolucion(Long entradaId, Long requesterUsuarioId);
}
