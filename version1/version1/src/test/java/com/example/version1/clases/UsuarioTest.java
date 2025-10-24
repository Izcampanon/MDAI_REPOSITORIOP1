// UsuarioTest.java
package com.example.version1.clases;

import com.example.version1.model.*;
import com.example.version1.repository.*;
import jakarta.persistence.EntityManager;
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


        @Test
        void BorrarUsuario() {
            // 1. Crear y guardar datos de prueba usando save()
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
            compraEntrada = compraEntradaRepository.save(compraEntrada);

            // 2. Verificar que los datos existen antes de borrar
            Long usuarioId = usuario.getId();
            Long compraId = compraEntrada.getId();
            Long entradaId = entrada.getId();

            assertTrue(usuarioRepository.existsById(usuarioId));
            assertTrue(compraEntradaRepository.existsById(compraId));
            assertTrue(entradaRepository.existsById(entradaId));

            // Verificar relaciones antes de borrar
            List<Compra_Entrada> comprasAntes = compraEntradaRepository.findByUsuarioId(usuarioId);
            List<Entrada> entradasAntes = entradaRepository.findByUsuarioId(usuarioId);

            assertFalse(comprasAntes.isEmpty(), "Debe haber compras asociadas al usuario");
            assertFalse(entradasAntes.isEmpty(), "Debe haber entradas asociadas al usuario");

            // 3. Borrar el usuario usando delete()
            usuarioRepository.deleteById(usuarioId);

            // 4. Verificar que el usuario y sus relaciones se han borrado
            assertFalse(usuarioRepository.existsById(usuarioId), "El usuario debería haber sido eliminado");

            // Verificar que las compras asociadas se han eliminado
            List<Compra_Entrada> comprasDespues = compraEntradaRepository.findByusuarioId_Compra(usuarioId);
            assertTrue(comprasDespues.isEmpty(), "Las compras del usuario deberían haberse eliminado");

            // Verificar que las entradas asociadas se han eliminado
            List<Entrada> entradasDespues = entradaRepository.findByUsuarioId(usuarioId);
            assertTrue(entradasDespues.isEmpty(), "Las entradas del usuario deberían haberse eliminado");

            // Verificar que otras entidades no relacionadas siguen existiendo
            assertTrue(eventoRepository.existsById(evento.getId()), "El evento debería seguir existiendo");
            assertTrue(localRepository.existsById(local.getId()), "El local debería seguir existiendo");
            assertTrue(ubicacionRepository.existsById(ubicacion.getId()), "La ubicación debería seguir existiendo");
        }
}


