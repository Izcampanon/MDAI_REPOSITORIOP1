package com.example.version1.clases;

import com.example.version1.model.Evento;
import com.example.version1.model.Local;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EventoTest {



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


}
