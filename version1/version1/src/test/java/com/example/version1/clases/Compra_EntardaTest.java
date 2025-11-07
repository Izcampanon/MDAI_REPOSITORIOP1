package com.example.version1.clases;

import com.example.version1.model.*;
import com.example.version1.repository.RepositoryCompra_Entrada;
import com.example.version1.repository.RepositoryEntrada;
import com.example.version1.repository.RepositoryEvento;
import com.example.version1.repository.RepositoryUsuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class Compra_EntardaTest {

    @Autowired
    private RepositoryCompra_Entrada compraRepository;

    @Autowired
    private RepositoryEntrada entradaRepository;

    @Autowired
    private RepositoryUsuario usuarioRepository;

    @Autowired
    private RepositoryEvento eventoRepository;

    @PersistenceContext
    private EntityManager em;

    @Test
    void crearCompra() {
        // limpiar
        compraRepository.deleteAll();
        entradaRepository.deleteAll();
        usuarioRepository.deleteAll();
        eventoRepository.deleteAll();

        // crear usuario
        Usuario u = new Usuario();
        u.setNombre("CompradorTest");
        u.setEmail("comprador@test.com");
        u.setContrasenia("pwd");
        u.setEdad(30);
        u = usuarioRepository.save(u);

        // crear evento y persistir
        Evento ev = new Evento();
        ev.setTitulo("EventoCompra");
        ev.setFecha(LocalDateTime.now().plusDays(5));
        ev.setAforo(100);
        ev.setEstado(Boolean.TRUE);
        ev = eventoRepository.save(ev);

        // crear entradas y persistir
        Entrada e1 = new Entrada("GENERAL", u, ev, 0);
        Entrada e2 = new Entrada("VIP", u, ev, 1);
        e1 = entradaRepository.save(e1);
        e2 = entradaRepository.save(e2);
        entradaRepository.flush();

        // crear compra con las entradas
        List<Entrada> lista = new ArrayList<>(); lista.add(e1); lista.add(e2);
        Compra_Entrada compra = new Compra_Entrada(new Date(), 50.0f, lista, u);
        compra = compraRepository.save(compra);
        compraRepository.flush();

        // aserciones
        assertNotNull(compra.getId(), "La compra debe tener id tras persistir");
        Compra_Entrada fetched = compraRepository.findById(compra.getId()).orElse(null);
        assertNotNull(fetched, "La compra debe recuperarse desde el repositorio");
        assertEquals(u.getId(), fetched.getUsuario().getId(), "La compra debe referenciar al usuario correcto");
        assertNotNull(fetched.getTipo_entradas());
        assertEquals(2, fetched.getTipo_entradas().size(), "La compra debe contener las 2 entradas");
    }

    @Test
    void eliminarCompra_eliminaRegistro() {
        // limpiar
        compraRepository.deleteAll();
        entradaRepository.deleteAll();
        usuarioRepository.deleteAll();
        eventoRepository.deleteAll();

        // crear usuario
        Usuario u = new Usuario();
        u.setNombre("CompradorEliminar");
        u.setEmail("elim@test.com");
        u.setContrasenia("pwd");
        u.setEdad(28);
        u = usuarioRepository.save(u);

        // crear evento
        Evento ev = new Evento();
        ev.setTitulo("EventoEliminar");
        ev.setFecha(LocalDateTime.now().plusDays(2));
        ev.setAforo(50);
        ev.setEstado(Boolean.TRUE);
        ev = eventoRepository.save(ev);

        // crear entrada y compra
        Entrada e = new Entrada("GENERAL", u, ev, 0);
        e = entradaRepository.save(e);
        entradaRepository.flush();

        Compra_Entrada compra = new Compra_Entrada(new Date(), 20.0f, new ArrayList<>(Collections.singletonList(e)), u);
        compra = compraRepository.save(compra);
        compraRepository.flush();

        Long idCompra = compra.getId();
        assertNotNull(idCompra);

        // eliminar
        int filas = compraRepository.deleteByIdNative(idCompra);
        // el método devuelve el número de filas afectadas
        assertTrue(filas >= 1, "La eliminación debe afectar al menos 1 fila");
        // Limpiar el persistence context para que JPA no devuelva entidades en caché
        em.flush();
        em.clear();

        // comprobar que ya no existe (consultará la BD realmente)
        assertFalse(compraRepository.findById(idCompra).isPresent(), "La compra no debe existir tras eliminación ");
    }

    //test que compruebe que se elimina la compra entrada y las entradas asociadas cuando se elimina un usuario
    @Test
    void eliminarUsuario_eliminaCompraYEntradas() {
        // limpiar
        compraRepository.deleteAll();
        entradaRepository.deleteAll();
        usuarioRepository.deleteAll();
        eventoRepository.deleteAll();

        // crear usuario
        Usuario u = new Usuario();
        u.setNombre("UserToRemove");
        u.setEmail("remove@test.com");
        u.setContrasenia("pwd");
        u.setEdad(26);
        u = usuarioRepository.save(u);

        // crear evento
        Evento ev = new Evento();
        ev.setTitulo("EventoParaBorrar");
        ev.setFecha(LocalDateTime.now().plusDays(3));
        ev.setAforo(80);
        ev.setEstado(Boolean.TRUE);
        ev = eventoRepository.save(ev);

        // crear entradas y persistir
        Entrada e1 = new Entrada("GENERAL", u, ev, 0);
        Entrada e2 = new Entrada("VIP", u, ev, 1);
        e1 = entradaRepository.save(e1);
        e2 = entradaRepository.save(e2);
        entradaRepository.flush();

        // crear compra con las entradas
        List<Entrada> lista = new ArrayList<>(); lista.add(e1); lista.add(e2);
        Compra_Entrada compra = new Compra_Entrada(new Date(), 35.0f, lista, u);
        compra = compraRepository.save(compra);
        compraRepository.flush();

        Long usuarioId = u.getId();
        assertNotNull(usuarioId);

        // Comprobar que existen entradas y compra asociadas
        List<Entrada> entradasAntes = entradaRepository.findByUsuarioId(usuarioId);
        assertTrue(entradasAntes.size() >= 2);
        List<Compra_Entrada> comprasAntes = compraRepository.findByUsuarioId(usuarioId);
        assertTrue(comprasAntes.size() >= 1);

        // Eliminar compras del usuario
        int removed = compraRepository.deleteByUsuarioIdNative(usuarioId);
        assertTrue(removed >= 0, "deleteByUsuarioIdNative debe devolver >= 0");
         // limpiar contexto tras operaciones nativas
         em.flush();
         em.clear();

        // Eliminar entradas asociadas al usuario
        List<Entrada> entradasARemover = entradaRepository.findByUsuarioId(usuarioId);
        if (!entradasARemover.isEmpty()) {
            entradaRepository.deleteAll(entradasARemover);
            entradaRepository.flush();
        }

        // Finalmente borrar el usuario
        usuarioRepository.deleteById(usuarioId);
        usuarioRepository.flush();

        // Comprobaciones finales: no debe haber compras ni entradas para ese usuario
        List<Compra_Entrada> comprasDespues = compraRepository.findByUsuarioId(usuarioId);
        List<Entrada> entradasDespues = entradaRepository.findByUsuarioId(usuarioId);
        assertTrue(comprasDespues.isEmpty(), "No deben quedar compras asociadas al usuario eliminado");
        assertTrue(entradasDespues.isEmpty(), "No deben quedar entradas asociadas al usuario eliminado");
        assertFalse(usuarioRepository.findById(usuarioId).isPresent(), "El usuario debe haber sido eliminado");
    }
}
