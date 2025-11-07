package com.example.version1.clases;

import com.example.version1.model.Compra_Entrada;
import com.example.version1.model.Entrada;
import com.example.version1.model.Evento;
import com.example.version1.model.Local;
import com.example.version1.model.Ubicacion;
import com.example.version1.model.Usuario;
import com.example.version1.repository.RepositoryCompra_Entrada;
import com.example.version1.repository.RepositoryEntrada;
import com.example.version1.repository.RepositoryEvento;
import com.example.version1.repository.RepositoryLocal;
import com.example.version1.repository.RepositoryUbicacion;
import com.example.version1.repository.RepositoryUsuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class TestGeneral {

    @Autowired
    RepositoryUsuario usuarioRepository;

    @Autowired
    RepositoryUbicacion ubicacionRepository;

    @Autowired
    RepositoryLocal localRepository;

    @Autowired
    RepositoryEvento eventoRepository;

    @Autowired
    RepositoryEntrada entradaRepository;

    @Autowired
    RepositoryCompra_Entrada compraRepository;

    @Test
    void testDeCasoGeneral() {
        // 1) Crear un usuario y comprobar que su email no existe ya
        usuarioRepository.deleteAll();
        ubicacionRepository.deleteAll();
        localRepository.deleteAll();
        eventoRepository.deleteAll();
        entradaRepository.deleteAll();
        compraRepository.deleteAll();

        Usuario user = new Usuario();
        user.setNombre("Cliente");
        user.setEmail("cliente@example.com");
        user.setContrasenia("pwd");
        user.setEdad(25);
        user = usuarioRepository.save(user);

        // Comprobación de unicidad de email
        boolean exists = usuarioRepository.existsByEmail("cliente@example.com");
        // Después de guardar el usuario, el email debe existir (case-insensitive)
        assertTrue(exists, "El email del usuario guardado debe reportarse como existente");
        // Intentar registrar otro usuario con mismo email -> debe detectarse
        Usuario dup = new Usuario(); dup.setNombre("Otro"); dup.setEmail("CLIENTE@example.com"); dup.setContrasenia("x"); dup.setEdad(30);
        boolean emailTaken = usuarioRepository.existsByEmail(dup.getEmail());
        assertTrue(emailTaken, "El email ya debe estar registrado (case-insensitive)");

        // 2) Usuario introduce nombre de ubicacion; comprobar que existe
        Ubicacion ub = new Ubicacion(null, "Valencia");
        Local l1 = new Local(null, "Local Uno");
        Local l2 = new Local(null, "Local Dos");
        ub.addLocal(l1);
        ub.addLocal(l2);
        ub = ubicacionRepository.save(ub);

        // Comprobación alternativa: la ubicacion guardada debe poder recuperarse y contener locales
        Ubicacion savedUb = ubicacionRepository.findById(ub.getId()).orElseThrow();
        assertNotNull(savedUb);
        assertNotNull(savedUb.getLocales());
        assertEquals(2, savedUb.getLocales().size());

        // 4) Usuario selecciona un local y se muestran eventos disponibles (usamos la instancia gestionada)
        Local chosenLocal = savedUb.getLocales().get(0);

        // Crear eventos: uno disponible, otro no disponible (fecha pasada), otro no disponible (estado false)
        Evento evAvail = new Evento(); evAvail.setTitulo("EvDisponible"); evAvail.setFecha(LocalDateTime.now().plusDays(5)); evAvail.setAforo(100); evAvail.setEstado(Boolean.TRUE);
        Evento evPast = new Evento(); evPast.setTitulo("EvPasado"); evPast.setFecha(LocalDateTime.now().minusDays(3)); evPast.setAforo(100); evPast.setEstado(Boolean.FALSE);
        Evento evNo = new Evento(); evNo.setTitulo("EvNoDisp"); evNo.setFecha(LocalDateTime.now().plusDays(2)); evNo.setAforo(100); evNo.setEstado(Boolean.FALSE);

        chosenLocal.addEvento(evAvail);
        chosenLocal.addEvento(evPast);
        chosenLocal.addEvento(evNo);
        // Guardar local con eventos (cascade persistirá los eventos)
        chosenLocal = localRepository.save(chosenLocal);
        localRepository.flush();

        // Recuperar eventos disponibles mediante el repositorio de Evento (asegurarnos de tomar los eventos persistidos)
        List<Evento> disponibles = eventoRepository.findDisponiblesByLocalId(chosenLocal.getId());
        // Para evitar referencias a entidades transientes, recuperamos las entidades Evento gestionadas desde el repositorio
        List<Evento> allEvents = eventoRepository.findByLocalId(chosenLocal.getId());
        Evento selected = allEvents.stream().filter(e -> "EvDisponible".equals(e.getTitulo())).findFirst().orElseThrow();
        Evento evNoManaged = allEvents.stream().filter(e -> "EvNoDisp".equals(e.getTitulo())).findFirst().orElseThrow();
        assertNotNull(disponibles);
        // Debe contener solo evAvail
        assertTrue(disponibles.stream().allMatch(e -> Boolean.TRUE.equals(e.getEstado())), "Solo eventos con estado true deben aparecer");
        assertTrue(disponibles.stream().anyMatch(e -> "EvDisponible".equals(e.getTitulo())));
        assertFalse(disponibles.stream().anyMatch(e -> "EvPasado".equals(e.getTitulo()) || "EvNoDisp".equals(e.getTitulo())));

        // 5) Usuario selecciona un evento (uno solo) y compra entradas (más de una)
        // 'selected' ya es la entidad gestionada recuperada más arriba (selected variable)

        // Crear 3 entradas para el usuario en ese evento
        Entrada ent1 = entradaRepository.save(new Entrada("GENERAL", user, selected, 0));
        Entrada ent2 = entradaRepository.save(new Entrada("GENERAL", user, selected, 0));
        Entrada ent3 = entradaRepository.save(new Entrada("VIP", user, selected, 0));
        entradaRepository.flush();

        // Capturar ids en variables efectivamente finales para usarlas dentro de lambdas
        final Long ent1Id = ent1.getId();
        final Long ent2Id = ent2.getId();
        final Long ent3Id = ent3.getId();

        List<Entrada> compradas = new ArrayList<>(); compradas.add(ent1); compradas.add(ent2); compradas.add(ent3);
        Compra_Entrada compra = new Compra_Entrada(new Date(), 75.0f, compradas, user);
        compra = compraRepository.save(compra);
        compraRepository.flush();

        // 6) Usuario comprueba la lista de entradas compradas
        List<Entrada> entradasUsuario = entradaRepository.findByUsuarioId(user.getId());
        assertNotNull(entradasUsuario);
        // Debe contener al menos las 3 entradas recién compradas
        assertTrue(entradasUsuario.stream().anyMatch(en -> en.getId() != null && en.getId().equals(ent1Id)));
        assertTrue(entradasUsuario.stream().anyMatch(en -> en.getId() != null && en.getId().equals(ent2Id)));
        assertTrue(entradasUsuario.stream().anyMatch(en -> en.getId() != null && en.getId().equals(ent3Id)));

        // 7) Usuario elimina la entrada que acaba de comprar (debe permitirse porque el evento está disponible)
        boolean deleted = entradaRepository.deleteIfEventAvailable(ent1Id);
        assertTrue(deleted, "Se debe permitir eliminar la entrada asociada a un evento disponible");
        assertFalse(entradaRepository.findById(ent1Id).isPresent(), "La entrada eliminada no debe existir");

        // 8) Intentar eliminar una entrada asociada a un evento no disponible (evNo) - debe rechazarse
        // Crear una entrada para evNo (usamos la entidad gestionada evNoManaged)
        Entrada entNo = entradaRepository.save(new Entrada("GENERAL", user, evNoManaged, 0));
        entradaRepository.flush();
        final Long entNoId = entNo.getId();

        // Attempt delete -> should be false
        boolean deletedNo = entradaRepository.deleteIfEventAvailable(entNoId);
        assertFalse(deletedNo, "No se debe permitir eliminar una entrada cuyo evento no está disponible");
        assertTrue(entradaRepository.findById(entNoId).isPresent(), "La entrada del evento no disponible debe permanecer");
    }
}
