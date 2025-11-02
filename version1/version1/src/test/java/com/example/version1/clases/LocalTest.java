package com.example.version1.clases;

import com.example.version1.model.Local;
import com.example.version1.model.Ubicacion;
import com.example.version1.repository.RepositoryLocal;
import com.example.version1.repository.RepositoryUbicacion;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class LocalTest {

    @Autowired
    private RepositoryLocal localRepository;

    @Autowired
    private RepositoryUbicacion ubicacionRepository;

    @Test
    void crearLocal_sePersisteYRelacionConUbicacion() {
        // Crear Ubicacion y Local, asociarlos mediante el helper para mantener ambas caras
        Ubicacion ubicacion = new Ubicacion(null, "Madrid");
        Local local = new Local(null, "Bar Central");
        ubicacion.addLocal(local);

        // Persistir la ubicacion (cascade persiste el Local)
        ubicacion = ubicacionRepository.save(ubicacion);

        // Verificaciones
        assertNotNull(ubicacion.getId(), "La ubicacion debe tener id tras persistir");
        assertNotNull(ubicacion.getLocales(), "La lista de locales no debe ser null");
        assertEquals(1, ubicacion.getLocales().size(), "Debe existir 1 local asociado a la ubicacion");

        Local savedLocal = ubicacion.getLocales().get(0);
        assertNotNull(savedLocal.getId(), "El local debe tener id tras persistir por cascade");
        assertEquals("Bar Central", savedLocal.getNombre(), "El nombre del local debe coincidir");

        // Recuperar mediante el repositorio de Local para confirmar persistencia independiente
        Local fetched = localRepository.findById(savedLocal.getId()).orElse(null);
        assertNotNull(fetched, "El local debe recuperarse desde RepositoryLocal");
        assertEquals("Madrid", fetched.getUbicacion().getNombre(), "La ubicacion asociada al local debe ser 'Madrid'");
    }

    @Test
    void eliminarLocal_seEliminaYActualizaUbicacion() {
        // 1. crear ubicacion con dos locales
        Ubicacion ubicacion = new Ubicacion(null, "Barcelona");
        Local local1 = new Local(null, "Sala A");
        Local local2 = new Local(null, "Sala B");
        ubicacion.addLocal(local1);
        ubicacion.addLocal(local2);

        // persistir ubicacion (cascade persiste locales)
        ubicacion = ubicacionRepository.save(ubicacion);

        // ids generados
        Long idLocal1 = local1.getId();
        Long idLocal2 = local2.getId();
        assertNotNull(idLocal1, "Local1 debe tener id tras persistir");
        assertNotNull(idLocal2, "Local2 debe tener id tras persistir");

        // sanity: ambos locales existen
        assertTrue(localRepository.findById(idLocal1).isPresent());
        assertTrue(localRepository.findById(idLocal2).isPresent());

        // 2. obtener el Local gestionado, quitarlo de la Ubicacion y eliminarlo
        Local managedLocal1 = localRepository.findById(idLocal1).orElseThrow();

        // quitar de la colección de Ubicacion para mantener sincronía en memoria
        ubicacion.removeLocal(managedLocal1);

        // borrar el local
        localRepository.delete(managedLocal1);
        localRepository.flush();
        ubicacionRepository.flush();

        // 3. comprobar que local1 ya no existe y local2 sí
        assertFalse(localRepository.findById(idLocal1).isPresent(), "El local eliminado no debe existir en el repositorio");
        assertTrue(localRepository.findById(idLocal2).isPresent(), "El otro local debe seguir existiendo");

        // 4. recuperar ubicacion y comprobar que la lista de locales se ha actualizado
        Ubicacion uRec = ubicacionRepository.findById(ubicacion.getId()).orElse(null);
        assertNotNull(uRec, "La ubicacion debe seguir existiendo");
        assertTrue(uRec.getLocales().stream().noneMatch(l -> l.getId() != null && l.getId().equals(idLocal1)), "La ubicacion no debe contener el local eliminado");
        assertTrue(uRec.getLocales().stream().anyMatch(l -> l.getId() != null && l.getId().equals(idLocal2)), "La ubicacion debe seguir conteniendo el local no eliminado");
    }

    @Test
    void comporbarEventosInicialesListaVacia() {
        Local local = new Local(null, "Teatro Principal");
        assertNotNull(local.getEventos(), "La lista de eventos no debe ser null al crear un Local");
        assertTrue(local.getEventos().isEmpty(), "La lista de eventos debe estar vacía al crear un Local");
    }

}
