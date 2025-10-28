// UsuarioTest.java
package com.example.version1.clases;

import com.example.version1.model.*;
import com.example.version1.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Id;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Date;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UsuarioTest {

    @Autowired
    private RepositoryUsuario usuarioRepository;

    @Autowired
    private RepositoryCompra_Entrada compraEntradaRepository;

    @Autowired
    private RepositoryEntrada entradaRepository;

    @Autowired
    private RepositoryEvento eventoRepository;

    @Autowired
    private RepositoryLocal localRepository;

    @Autowired
    private RepositoryUbicacion ubicacionRepository;

    @Test
    void crearUsuarioYVerificarAtributos() {

        // Crear usuario usando el constructor
        Usuario usuario = new Usuario("Juan", "juan@example.com", "pass123", true, "123456789");
        // Antes de persistir, el id debe ser null
        assertNull(usuario.getId());

        // Verificar atributos iniciales
        assertEquals("Juan", usuario.getNombre());
        assertEquals("juan@example.com", usuario.getEmail());
        assertEquals("pass123", usuario.getContrasenia());
        assertTrue(usuario.isEdad());
        assertEquals("123456789", usuario.getTelefono());

        // Probar setters
        usuario.setNombre("Pedro");
        assertEquals("Pedro", usuario.getNombre());

    }



    //Importante: Ya que Compra_Entrada tiene cascade = CascadeType.ALL para su relación con Entrada, y
    // Usuario tiene cascade para Compra_Entrada, la eliminación de un Usuario debería provocar la eliminación
    // en cascada de sus Compra_Entrada y, a su vez, la eliminación en cascada de todas las Entrada asociadas a esas compras.
    @Test
    void BorrarUsuario_EliminaCompraEntradaYEntradasEnCascada() {
        // 1. Crear y guardar datos de prueba
        Ubicacion ubicacion = new Ubicacion();
        ubicacion.setNombre("Madrid");
        ubicacion = ubicacionRepository.save(ubicacion);

        Local local = new Local();
        local.setNombre("Sala Copérnico");
        local.setUbicacion(ubicacion);
        local = localRepository.save(local);

        Usuario usuario = new Usuario();
        usuario.setNombre("Juan Pérez");
        usuario.setEmail("juan@example.com");
        usuario.setContrasenia("password");
        usuario.setEdad(true);
        usuario.setTelefono("123456789");
        usuario.setUbicacion(ubicacion);
        usuario = usuarioRepository.save(usuario);

        Evento evento = new Evento();
        evento.setTitulo("Concierto de Prueba");
        evento.setFecha(LocalDateTime.now().plusDays(1));
        evento.setArtista("Artista de Prueba");
        evento.setUbicacion("Madrid");
        evento.setDescripcion("Descripción del evento");
        evento.setAforo(100);
        evento.setEstado("disponible");
        evento.setTipo("+18");
        evento.setEstado_aforo(false);
        evento.setLocal(local);
        evento = eventoRepository.save(evento);

        Entrada entrada = new Entrada();
        entrada.setNombre_evento("Concierto de Prueba");
        entrada.setNombre_usuario("Juan Pérez");
        entrada.setConsumiciones(true);
        entrada.setTipo("VIP");
        entrada.setUsuario(usuario);
        entrada.setEvento(evento);
        entrada = entradaRepository.save(entrada);

        Compra_Entrada compraEntrada = new Compra_Entrada();
        compraEntrada.setFechaCompra(new Date());
        compraEntrada.setPrecio(50.0f);
        compraEntrada.setUsuario(usuario);
        compraEntrada.setTipo_entradas(List.of(entrada));

        // Sincronizar relación bidireccional (necesario si luego se accede a la lista)
        usuario.getEntardas_compradas().add(compraEntrada);
        // Persistir Compra_Entrada (el lado dueño de la FK)
        compraEntrada = compraEntradaRepository.save(compraEntrada);


        // 2. Verificar que los datos existen antes de borrar
        Long usuarioId = usuario.getId();
        Long compraId = compraEntrada.getId();
        Long entradaId = entrada.getId();

        assertTrue(usuarioRepository.existsById(usuarioId), "El usuario debe existir antes de la eliminación.");
        assertTrue(compraEntradaRepository.existsById(compraId), "La compra debe existir antes de la eliminación.");
        assertTrue(entradaRepository.existsById(entradaId), "La entrada debe existir antes de la eliminación.");

        // 3. Borrar el usuario
        usuarioRepository.deleteById(usuarioId);

        // 4. Verificar la eliminación en cascada
        assertFalse(usuarioRepository.existsById(usuarioId), "El usuario debería haber sido eliminado.");
        assertFalse(compraEntradaRepository.existsById(compraId), "La Compra_Entrada asociada debería haberse eliminado en cascada.");
        assertFalse(entradaRepository.existsById(entradaId), "La Entrada asociada debería haberse eliminado en cascada.");

        // Verificar que otras entidades no relacionadas siguen existiendo
        assertTrue(eventoRepository.existsById(evento.getId()), "El evento debería seguir existiendo.");
        assertTrue(localRepository.existsById(local.getId()), "El local debería seguir existiendo.");
        assertTrue(ubicacionRepository.existsById(ubicacion.getId()), "La ubicación debería seguir existiendo.");
    }
}


