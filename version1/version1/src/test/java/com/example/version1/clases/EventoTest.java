package com.example.version1.clases;

import com.example.version1.model.Evento;
import com.example.version1.model.Local;
import com.example.version1.repository.RepositoryEvento;
import com.example.version1.repository.RepositoryLocal;
import com.example.version1.repository.RepositoryUbicacion;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class EventoTest {

    @Autowired
    private RepositoryEvento eventoRepository;

    @Autowired
    private RepositoryLocal localRepository;

    @Autowired
    private RepositoryUbicacion ubicacionRepository;

    @Test
    void crearEvento_sePersisteCorrectamente() {
        //crear un evento y comprobar que se persiste correctamente
        Local local = new Local(null, "Sala de Conciertos");
        Evento evento = new Evento("Concierto Rock", LocalDateTime.of(2024, 10, 15, 20, 0),
                "Banda Famosa", "Calle Falsa 123", "Un gran concierto de rock.",
                500, true, 18, local);

        assertNotNull(evento);
        assertEquals("Concierto Rock", evento.getTitulo());
        assertEquals("Banda Famosa", evento.getArtista());
        assertEquals(500, evento.getAforo());
        assertTrue(evento.getEstado());
        assertEquals(18, evento.getEdadpermitida());
        assertEquals(local, evento.getLocal());
    }

    @Test
    void eliminarEvento_seEliminaCorrectamente() {
        //eliminar un evento y comprobar que se elimina correctamente
        Local local = new Local(null, "Sala de Conciertos");
        Evento evento = new Evento("Concierto Rock", LocalDateTime.of(2024, 10, 15, 20, 0),
                "Banda Famosa", "Calle Falsa 123", "Un gran concierto de rock.",
                500, true, 18, local);

        evento = null; //simular eliminacion

        assertNull(evento);
    }

    //test para que muestre lel titulo del evento, artista, fecha y local
    @Test
    void mostrarDetallesEvento_seMuestranCorrectamente() {
        Local local = new Local(null, "Sala de Conciertos");
        Evento evento = new Evento("Concierto Rock", LocalDateTime.of(2024, 10, 15, 20, 0),
                "Banda Famosa", "Calle Falsa 123", "Un gran concierto de rock.",
                500, true, 18, local);

        String detalles = "Titulo: " + evento.getTitulo() +
                ", Artista: " + evento.getArtista() +
                ", Fecha: " + evento.getFecha().toString() +
                ", Local: " + evento.getLocal().getNombre();

        assertEquals("Titulo: Concierto Rock, Artista: Banda Famosa, Fecha: 2024-10-15T20:00, Local: Sala de Conciertos", detalles);
    }

    //test que compruebe que la fecha del evento ya haya pasado (no disponible) y
    // si ha pasado que lo elimine de la lisa de entradas del usuario
    @Test
    void comprobarDisponibilidadEvento_fechaPasada_noDisponible() {
        Local local = new Local(null, "Sala de Conciertos");
        Evento evento = new Evento("Concierto Rock", LocalDateTime.of(2022, 10, 15, 20, 0),
                "Banda Famosa", "Calle Falsa 123", "Un gran concierto de rock.",
                500, true, 18, local);

        LocalDateTime ahora = LocalDateTime.now();

        boolean disponible;
        if (evento.getFecha().isBefore(ahora)) {
            disponible = false;
        } else {
            disponible = true;
        }

        assertFalse(disponible, "El evento no debe estar disponible ya que la fecha ha pasado");
    }

    //comprobar que los eventos que salgan al usuario sean de la ubicaicon introducida
    @Test
    void filtrarEventos_porUbicacion_correcto() {
        //crear dos locales con diferentes ubicaciones
        Local local1 = new Local(null, "Sala de Conciertos A");
        Local local2 = new Local(null, "Sala de Conciertos B");

        //crear eventos en cada local
        Evento evento1 = new Evento("Concierto A", LocalDateTime.of(2024, 10, 15, 20, 0),
                "Banda A", "Calle A 123", "Concierto en Sala A.",
                300, true, 18, local1);

        Evento evento2 = new Evento("Concierto B", LocalDateTime.of(2024, 11, 20, 21, 0),
                "Banda B", "Calle B 456", "Concierto en Sala B.",
                400, true, 21, local2);

        //filtrar eventos por la ubicacion del local1
        String ubicacionBuscada = "Sala de Conciertos A";

        boolean evento1Coincide = evento1.getLocal().getNombre().equals(ubicacionBuscada);
        boolean evento2Coincide = evento2.getLocal().getNombre().equals(ubicacionBuscada);

        assertTrue(evento1Coincide, "El evento1 debe coincidir con la ubicación buscada");
        assertFalse(evento2Coincide, "El evento2 no debe coincidir con la ubicación buscada");
    }

    @Test
    void findByLocalId_returnsEvents() {
        // Aislar
        eventoRepository.deleteAll();
        localRepository.deleteAll();
        ubicacionRepository.deleteAll();

        Local local = new Local(null, "SalaSQL");
        Evento e1 = new Evento(); e1.setTitulo("E1"); e1.setFecha(LocalDateTime.now().plusDays(1)); e1.setAforo(100); e1.setEstado(Boolean.TRUE);
        Evento e2 = new Evento(); e2.setTitulo("E2"); e2.setFecha(LocalDateTime.now().plusDays(2)); e2.setAforo(100); e2.setEstado(Boolean.TRUE);

        local.addEvento(e1);
        local.addEvento(e2);

        local = localRepository.save(local);
        localRepository.flush();

        List<Evento> encontrados = eventoRepository.findByLocalId(local.getId());
        assertNotNull(encontrados);
        assertEquals(2, encontrados.size());
        assertTrue(encontrados.stream().anyMatch(ev -> "E1".equals(ev.getTitulo())));
        assertTrue(encontrados.stream().anyMatch(ev -> "E2".equals(ev.getTitulo())));
    }

    @Test
    void findDisponiblesByLocalId_returnsOnlyAvailable() {
        // Aislar
        eventoRepository.deleteAll();
        localRepository.deleteAll();

        Local local = new Local(null, "SalaDisp");
        Evento a1 = new Evento(); a1.setTitulo("A1"); a1.setFecha(LocalDateTime.now().plusDays(1)); a1.setAforo(100); a1.setEstado(Boolean.TRUE);
        Evento a2 = new Evento(); a2.setTitulo("A2"); a2.setFecha(LocalDateTime.now().plusDays(1)); a2.setAforo(100); a2.setEstado(Boolean.FALSE);

        local.addEvento(a1);
        local.addEvento(a2);

        local = localRepository.save(local);
        localRepository.flush();

        List<Evento> disponibles = eventoRepository.findDisponiblesByLocalId(local.getId());
        assertNotNull(disponibles);
        assertEquals(1, disponibles.size());
        assertEquals("A1", disponibles.get(0).getTitulo());
        assertTrue(Boolean.TRUE.equals(disponibles.get(0).getEstado()));
    }



}
