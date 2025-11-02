// UsuarioTest.java
package com.example.version1.clases;

import com.example.version1.model.*;
import com.example.version1.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Id;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.Arrays;
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

    @Test
    void crearCompraDesdeUsuario_seAsociaAUsuario() {
        // 1. Crear y persistir Ubicacion y Local (solo para completar datos si es necesario)
        Ubicacion ubicacion = new Ubicacion();
        ubicacion.setNombre("Zaragoza");
        ubicacion = ubicacionRepository.save(ubicacion);

        Local local = new Local();
        local.setNombre("Sala Test");
        local.setUbicacion(ubicacion);
        local = localRepository.save(local);

        // 2. Crear y persistir Usuario
        Usuario usuario = new Usuario();
        usuario.setNombre("Lucia");
        usuario.setEmail("lucia@test.com");
        usuario.setContrasenia("pwd");
        usuario.setEdad(true);
        usuario.setTelefono("600111222");
        usuario.setUbicacion(ubicacion);
        usuario = usuarioRepository.save(usuario);
        assertNotNull(usuario.getId(), "Usuario debe tener id tras persistir");

        // 3. Crear y persistir Evento
        Evento evento = new Evento();
        evento.setTitulo("Evento Test");
        evento.setFecha(LocalDateTime.now().plusDays(5));
        evento.setArtista("Artista Test");
        evento.setUbicacion("Zaragoza");
        evento.setDescripcion("Desc");
        evento.setAforo(200);
        evento.setEstado("disponible");
        evento.setTipo("+18");
        evento.setEstado_aforo(false);
        evento.setLocal(local);
        evento = eventoRepository.save(evento);
        assertNotNull(evento.getId(), "Evento debe tener id tras persistir");

        // 4. Crear Entradas y Compra_Entrada asociadas al usuario
        Entrada entrada = new Entrada();
        entrada.setNombre_evento(evento.getTitulo());
        entrada.setNombre_usuario(usuario.getNombre());
        entrada.setConsumiciones(false);
        entrada.setTipo("GENERAL");
        entrada.setUsuario(usuario);
        entrada.setEvento(evento);

        Compra_Entrada compra = new Compra_Entrada();
        compra.setFechaCompra(new Date());
        compra.setPrecio(25.0f);
        compra.setTipo_entradas(List.of(entrada));
        compra.setUsuario(usuario);

        // 5. Mantener la relación bidireccional en memoria
        usuario.getEntardas_compradas().add(compra);

        // 6. Persistir la compra (cascade) y verificar
        compra = compraEntradaRepository.save(compra);
        assertNotNull(compra.getId(), "La compra debe tener id tras persistir");

        // 7. Recargar usuario y comprobar que la compra aparece en su colección
        Usuario usuarioRec = usuarioRepository.findById(usuario.getId()).orElse(null);
        assertNotNull(usuarioRec, "El usuario debe recuperarse");
        assertNotNull(usuarioRec.getEntardas_compradas(), "La colección de compras no debe ser null");
        assertEquals(1, usuarioRec.getEntardas_compradas().size(), "El usuario debe tener 1 compra asociada");
        Compra_Entrada compraRec = usuarioRec.getEntardas_compradas().get(0);
        assertEquals(compra.getId(), compraRec.getId(), "La compra asociada al usuario debe coincidir con la persistida");
    }

    @Test
    void anularCompra_usuarioEliminaCompraYEntradas() {
        // 1) Crear y persistir Ubicacion/Local/Usuario/Eventos (mínimo necesario)
        Ubicacion u = new Ubicacion();
        u.setNombre("PruebaCity");
        u = ubicacionRepository.save(u);

        Local local = new Local();
        local.setNombre("Sala Prueba");
        local.setUbicacion(u);
        local = localRepository.save(local);

        Usuario usuario = new Usuario("Cliente", "c@test.com", "pwd", true, "600000000");
        usuario.setUbicacion(u);
        usuario = usuarioRepository.save(usuario);

        Evento evento = new Evento("Show", LocalDateTime.now().plusDays(2), "Art", "PruebaCity", "desc", 100, "disponible", "+18", false, local);
        evento = eventoRepository.save(evento);

        // 2) Crear Entradas y Compra_Entrada asociadas al usuario
        Entrada e1 = new Entrada("Show", usuario.getNombre(), false, "GENERAL", usuario, evento);
        Compra_Entrada compra = new Compra_Entrada(new Date(), 20.0f, new ArrayList<>(Arrays.asList(e1)), usuario);

        // Mantener bidireccionalidad en memoria (si aplica)
        usuario.getEntardas_compradas().add(compra);

        // Persistir la compra (cascade persistirá las entradas)
        compra = compraEntradaRepository.save(compra);

        Long compraId = compra.getId();
        Long entradaId = e1.getId();
        Long usuarioId = usuario.getId();

        assertTrue(compraEntradaRepository.existsById(compraId));
        assertTrue(entradaRepository.existsById(entradaId));

        // 3) Simular anulación por parte del usuario:
        // - quitar la compra de la colección del usuario para mantener coherencia en memoria
        Usuario managedUser = usuarioRepository.findById(usuarioId).orElseThrow();
        Compra_Entrada managedCompra = compraEntradaRepository.findById(compraId).orElseThrow();

        managedUser.getEntardas_compradas().removeIf(c -> c.getId().equals(compraId));
        usuarioRepository.save(managedUser); // opcional para sincronizar lado propietario si usas orphanRemoval

        // Borrar la compra (si tu diseño requiere borrado explícito)
        compraEntradaRepository.deleteById(compraId);
        compraEntradaRepository.flush();

        // 4) Comprobaciones: compra y entradas ya no existen y usuario no las referencia
        assertFalse(compraEntradaRepository.existsById(compraId), "La compra debe estar eliminada");
        assertFalse(entradaRepository.existsById(entradaId), "Las entradas asociadas deben ser eliminadas en cascada (si ese es el contrato)");
        Usuario usuarioRec = usuarioRepository.findById(usuarioId).orElseThrow();
        assertTrue(usuarioRec.getEntardas_compradas().stream().noneMatch(c -> c.getId().equals(compraId)),
                "La colección de compras del usuario no debe contener la compra anulada");
    }


}
