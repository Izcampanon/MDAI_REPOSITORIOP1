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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class PruebaCRUD {

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
    void flujoCRUD_basico() {
        // Limpiar datos para dejar estado inicial
        compraRepository.deleteAll();
        entradaRepository.deleteAll();
        eventoRepository.deleteAll();
        localRepository.deleteAll();
        ubicacionRepository.deleteAll();
        usuarioRepository.deleteAll();

        // --- CREATE: crear un usuario ---
        Usuario usuario = new Usuario();
        usuario.setNombre("Ana");
        usuario.setEmail("ana@example.com");
        usuario.setContrasenia("secreta");
        usuario.setEdad(28);
        usuario = usuarioRepository.save(usuario);
        assertNotNull(usuario.getId(), "El usuario creado debe tener ID");

        // --- READ: leer por id y comprobar email ---
        Optional<Usuario> leido = usuarioRepository.findById(usuario.getId());
        assertTrue(leido.isPresent(), "Debe encontrarse el usuario por id");
        assertEquals("ana@example.com", leido.get().getEmail());

        // Verificar existencia por email (método nativo ya usado en otros tests)
        boolean existeEmail = usuarioRepository.existsByEmail("ANA@example.com");
        assertTrue(existeEmail, "La comprobación de email debe ser case-insensitive y devolver true");

        // --- UPDATE: modificar nombre y guardar ---
        Usuario paraActualizar = leido.get();
        paraActualizar.setNombre("Ana María");
        usuarioRepository.save(paraActualizar);

        Usuario actualizado = usuarioRepository.findById(usuario.getId()).orElseThrow();
        assertEquals("Ana María", actualizado.getNombre(), "El nombre debe haberse actualizado");

        // --- CREATE: Ubicacion y Local ---
        Ubicacion ubic = new Ubicacion(null, "Sevilla");
        Local local = new Local(null, "Sala Principal");
        ubic.addLocal(local);
        ubic = ubicacionRepository.save(ubic);
        assertNotNull(ubic.getId(), "Ubicacion guardada debe tener id");
        assertFalse(ubic.getLocales().isEmpty(), "La ubicacion debe contener locales");

        // --- CREATE: Evento asociado al local ---
        Local localGestionado = ubic.getLocales().get(0);
        Evento evento = new Evento();
        evento.setTitulo("Concierto CRUD");
        evento.setFecha(LocalDateTime.now().plusDays(7));
        evento.setAforo(200);
        evento.setEstado(Boolean.TRUE);
        localGestionado.addEvento(evento);
        localGestionado = localRepository.save(localGestionado);
        localRepository.flush();

        List<Evento> eventosDelLocal = eventoRepository.findByLocalId(localGestionado.getId());
        assertTrue(eventosDelLocal.stream().anyMatch(e -> "Concierto CRUD".equals(e.getTitulo())));

        // Seleccionamos el evento gestionado
        Evento eventoGestionado = eventosDelLocal.stream().filter(e -> "Concierto CRUD".equals(e.getTitulo())).findFirst().orElseThrow();

        // --- CREATE: Entradas para el usuario ---
        Entrada entrada = entradaRepository.save(new Entrada("GENERAL", usuario, eventoGestionado, 0));
        assertNotNull(entrada.getId(), "Entrada creada debe tener id");

        // --- READ: entradas del usuario ---
        List<Entrada> entradasUsuario = entradaRepository.findByUsuarioId(usuario.getId());
        assertTrue(entradasUsuario.stream().anyMatch(e -> e.getId().equals(entrada.getId())));

        // --- UPDATE: cambiar tipo de entrada ---
        entrada.setTipo("VIP");
        entradaRepository.save(entrada);
        Entrada entradaAct = entradaRepository.findById(entrada.getId()).orElseThrow();
        assertEquals("VIP", entradaAct.getTipo());

        // --- DELETE: borrar la entrada ---
        entradaRepository.deleteById(entrada.getId());
        assertFalse(entradaRepository.findById(entrada.getId()).isPresent(), "La entrada debe haberse eliminado");

        // --- DELETE: borrar el usuario ---
        usuarioRepository.deleteById(usuario.getId());
        assertFalse(usuarioRepository.findById(usuario.getId()).isPresent(), "El usuario debe haberse eliminado");


        // Operación final: crear una compra con varias entradas (demostración de relación)
        Entrada e1 = entradaRepository.save(new Entrada("GENERAL", updatedOrFallback(actualizado), eventoGestionado, 0));
        Entrada e2 = entradaRepository.save(new Entrada("VIP", updatedOrFallback(actualizado), eventoGestionado, 0));
        entradaRepository.flush();
        List<Entrada> listaEntradas = new ArrayList<>();
        listaEntradas.add(e1);
        listaEntradas.add(e2);
        Compra_Entrada compra = new Compra_Entrada(new Date(), 120.0f, listaEntradas, updatedOrFallback(actualizado));
        compra = compraRepository.save(compra);
        assertNotNull(compra.getId(), "La compra debe haberse guardado");
    }

    // Helper para manejar el caso en que el usuario fue eliminado anteriormente en el test.
    private Usuario updatedOrFallback(Usuario posible) {
        if (posible == null) return null;
        // Si el usuario ya no existe en BD, intentar recuperar por email; si no existe, crear uno temporal.
        Optional<Usuario> encontrado = posible.getId() != null ? usuarioRepository.findById(posible.getId()) : Optional.empty();
        if (encontrado.isPresent()) return encontrado.get();
        Optional<Usuario> porEmail = usuarioRepository.findByEmailIgnoreCase(posible.getEmail());
        if (porEmail.isPresent()) return porEmail.get();
        Usuario nuevo = new Usuario();
        nuevo.setNombre(posible.getNombre());
        nuevo.setEmail(posible.getEmail());
        nuevo.setContrasenia(posible.getContrasenia());
        nuevo.setEdad(posible.getEdad());
        return usuarioRepository.save(nuevo);
    }
}
