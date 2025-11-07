package com.example.version1.repository;

import com.example.version1.model.Usuario;
import com.example.version1.model.Compra_Entrada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface RepositoryUsuario extends JpaRepository<Usuario, Long> {

    // Buscar usuario por nombre
    @Query(value = "SELECT * FROM Usuario u WHERE lower(u.nombre) = lower(:nombre)", nativeQuery = true)
    Optional<Usuario> findByNombreIgnoreCase(@Param("nombre") String nombre);

    // Buscar usuario por email
    @Query(value = "SELECT * FROM Usuario u WHERE lower(u.email) = lower(:email)", nativeQuery = true)
    Optional<Usuario> findByEmailIgnoreCase(@Param("email") String email);

    // Contar emails iguales (para comprobación de existencia)
    //no puede existir dos usuarios con el mismo email
    @Query(value = "SELECT count(*) FROM Usuario u WHERE lower(u.email) = lower(:email)", nativeQuery = true)
    long countByEmailIgnoreCase(@Param("email") String email);

    // Existe email
    default boolean existsByEmail(@Param("email") String email) {
        return countByEmailIgnoreCase(email) > 0L;
    }

    // Usuarios por id de ubicacion
    @Query(value = "SELECT * FROM Usuario u WHERE u.ubicacion_id = :ubicacionId", nativeQuery = true)
    List<Usuario> findByUbicacionId(@Param("ubicacionId") Long ubicacionId);

    // Usuarios por edad mínima
    @Query(value = "SELECT * FROM Usuario u WHERE u.edad >= :edad", nativeQuery = true)
    List<Usuario> findByEdadGreaterEqual(@Param("edad") int edad);

    // Obtener compras asociadas a un usuario, devuelve entidad Compra_Entrada
    @Query(value = "SELECT * FROM Compra_Entrada c WHERE c.usuario_id = :usuarioId", nativeQuery = true)
    List<Compra_Entrada> findComprasByUsuarioId(@Param("usuarioId") Long usuarioId);

}
