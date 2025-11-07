package com.example.version1.repository;

import com.example.version1.model.Compra_Entrada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface RepositoryCompra_Entrada extends JpaRepository<Compra_Entrada, Long> {

    //compras por id de usuario
    @Query(value = "SELECT * FROM Compra_Entrada c WHERE c.usuario_id = :usuarioId", nativeQuery = true)
    List<Compra_Entrada> findByUsuarioId(@Param("usuarioId") Long usuarioId);

    // Eliminar filas de la tabla de unión por compra_id
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM compra_entrada_entardas WHERE compra_entrada_id = :id", nativeQuery = true)
    int deleteJoinRowsByCompraId(@Param("id") Long id);

    // Eliminar la fila de la compra en la tabla principal
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM Compra_Entrada WHERE id = :id", nativeQuery = true)
    int deleteCompraRow(@Param("id") Long id);

    // Método por defecto que limpia la join table y luego la compra; devuelve número de filas totales afectadas
    default int deleteByIdAfectadas(Long id) {
        int removedJoin = deleteJoinRowsByCompraId(id);
        int removedCompra = deleteCompraRow(id);
        return removedJoin + removedCompra;
    }

    // Alias para compatibilidad con tests: delega en deleteByIdAfectadas
    default int deleteByIdNative(Long id) {
        return deleteByIdAfectadas(id);
    }

    // Eliminar todas las filas de la join table relacionadas con compras de un usuario
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM compra_entrada_entardas WHERE compra_entrada_id IN (SELECT id FROM Compra_Entrada WHERE usuario_id = :usuarioId)", nativeQuery = true)
    int deleteJoinRowsByUsuarioId(@Param("usuarioId") Long usuarioId);

    // Eliminar todas las filas de compra para un usuario
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM Compra_Entrada WHERE usuario_id = :usuarioId", nativeQuery = true)
    int deleteCompraRowsByUsuarioId(@Param("usuarioId") Long usuarioId);

    // Conveniencia: primero limpiar join table, luego las compras; devuelve total de filas afectadas
    default int deleteByUsuarioIdNative(Long usuarioId) {
        int removedJoin = deleteJoinRowsByUsuarioId(usuarioId);
        int removedCompra = deleteCompraRowsByUsuarioId(usuarioId);
        return removedJoin + removedCompra;
    }

}
