package com.example.version1.clases;

import com.example.version1.model.Local;
import com.example.version1.model.Ubicacion;
import com.example.version1.model.Usuario;
import com.example.version1.repository.RepositoryLocal;
import com.example.version1.repository.RepositoryUbicacion;
import com.example.version1.repository.RepositoryUsuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UbicacionTest {

    @Autowired
    private RepositoryUbicacion ubicacionRepository;

    @Autowired
    private RepositoryUsuario usuarioRepository;

    @Autowired
    private RepositoryLocal localRepository;


    //Verificar si se crean correctamente los datos y funcionan los getters y setters
    @Test
    void crearyverificaratributos() {
        //Creamos una ubicaicon de ejemplo
        Ubicacion ubicacion = new Ubicacion(null, "Merida");

        //Verificamos los atributos
        assertNull(ubicacion.getId());
        assertEquals("Merida", ubicacion.getNombre());

        //Persistir entidad y verificar id generado
        ubicacion = ubicacionRepository.save(ubicacion);

        //Verificamos que el id no es nulo
        assertNotNull(ubicacion.getId());

        ubicacion.setNombre("Badajoz");
        assertEquals("Badajoz", ubicacion.getNombre());

        //Guardamos el cambio
        ubicacionRepository.save(ubicacion);

        //Recuperamos para ver si se guardo correctamente
        Ubicacion ubicacionRecuperada = ubicacionRepository.findById(ubicacion.getId()).orElse(null);
        assertNotNull(ubicacionRecuperada);
        assertEquals("Badajoz", ubicacionRecuperada.getNombre());

    }

    //test que compruebe que se puede eliminar una ubicacion
    @Test
    void eliminarUbicacion() {
        // Crear y guardar una Ubicacion
        Ubicacion ubicacion = new Ubicacion(null, "Barcelona");
        ubicacion = ubicacionRepository.save(ubicacion);

        // Verificar que la Ubicacion se ha guardado
        Long id = ubicacion.getId();
        assertNotNull(id, "La ubicacion debe tener un id asignado");

        // Eliminar la Ubicacion
        ubicacionRepository.delete(ubicacion);

        // Verificar que la Ubicacion se ha eliminado
        Ubicacion ubicacionEliminada = ubicacionRepository.findById(id).orElse(null);
        assertNull(ubicacionEliminada, "La ubicacion debe haber sido eliminada");
    }

    //test que muesntre lista de locales que tiene una ubicacion
    @Test
    void listaLocalesPorUbicacion() {
        // Crear y guardar una Ubicacion
        Ubicacion ubicacion = new Ubicacion(null, "Madrid");
        ubicacion = ubicacionRepository.save(ubicacion);

        // Crear y asociar locales a la ubicacion usando el helper
        Local local1 = new Local(null, "Local 1");
        ubicacion.addLocal(local1);
        Local local2 = new Local(null, "Local 2");
        ubicacion.addLocal(local2);
        localRepository.saveAll(Arrays.asList(local1, local2));

        // Recuperar la Ubicacion y verificar la lista de locales
        Ubicacion ubicacionRecuperada = ubicacionRepository.findById(ubicacion.getId()).orElse(null);
        assertNotNull(ubicacionRecuperada, "La ubicacion debe recuperarse");
        List<Local> locales = ubicacionRecuperada.getLocales();
        assertNotNull(locales, "La lista de locales no debe ser null");
        assertEquals(2, locales.size(), "Debe haber 2 locales asociados a la ubicacion");
        assertTrue(locales.stream().anyMatch(l -> "Local 1".equals(l.getNombre())));
        assertTrue(locales.stream().anyMatch(l -> "Local 2".equals(l.getNombre())));
    }


    //test que valide si al seleccinar la ubicaicon de un usuario puedo obtener
    //una lista de los locales que tengan esa ubicacion
    @Test
    void alObtenerUbicacionDesdeUsuario_incluyeListaDeLocales() {
        // 1. Crear y guardar ubicacion
        Ubicacion ubicacion = new Ubicacion(null, "Sevilla");
        ubicacion = ubicacionRepository.save(ubicacion);

        // 2. Crear y asociar locales a la ubicacion usando el helper
        Local local1 = new Local(null, "Bar A");
        ubicacion.addLocal(local1);
        Local local2 = new Local(null, "Bar B");
        ubicacion.addLocal(local2);
        localRepository.saveAll(Arrays.asList(local1, local2));

        // 3. Crear y guardar usuario asociado a la misma ubicacion
        Usuario usuario = new Usuario("Pepito", "pepito@test.com", "pass", 25, "000");
        ubicacion.addUsuario(usuario);
        usuario = usuarioRepository.save(usuario);

        // 4. Recuperar usuario y obtener su ubicacion
        Usuario usuarioRecuperado = usuarioRepository.findById(usuario.getId()).orElse(null);
        assertNotNull(usuarioRecuperado, "El usuario debe recuperarse");
        Ubicacion ubicacionDesdeUsuario = usuarioRecuperado.getUbicacion();
        assertNotNull(ubicacionDesdeUsuario, "La ubicacion desde el usuario no debe ser null");

        // 5. Verificar que la lista de locales esté cargada y contenga los locales creados
        List<Local> locales = ubicacionDesdeUsuario.getLocales();
        assertNotNull(locales, "La lista de locales no debe ser null");
        assertEquals(2, locales.size(), "Debe haber 2 locales asociados a la ubicacion");
        assertTrue(locales.stream().anyMatch(l -> "Bar A".equals(l.getNombre())));
        assertTrue(locales.stream().anyMatch(l -> "Bar B".equals(l.getNombre())));
    }


    //hacer un test que verifique que la ubicacion que introduce el usuario
    //segun el nombre exisita.
    @Test
    void verificarExistenciaUbicacionPorNombre() {
        // 1. Crear y guardar la ubicacion de referencia
        Ubicacion ubicacion = new Ubicacion(null, "Granada");
        ubicacion = ubicacionRepository.save(ubicacion);

        // 2. Preparar captura de salida estándar
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(baos));

        try {
            //Caso el usuario introduce el mismo nombre ->debe existir
            Ubicacion entradaIgual = new Ubicacion(null, "Granada");
            ubicacion.existe(entradaIgual);
            String salida1 = baos.toString().trim();
            assertTrue(salida1.contains("La ubicacion existe"));

            // Limpiar buffer para la siguiente comprobación
            baos.reset();

            // Caso: el usuario introduce un nombre distinto -> no debe existir
            Ubicacion entradaDistinta = new Ubicacion(null, "Córdoba");
            ubicacion.existe(entradaDistinta);
            String salida2 = baos.toString().trim();
            assertTrue(salida2.contains("La ubicacion no existe"));
        } finally {
            // Restaurar salida estándar
            System.setOut(originalOut);
        }
    }
}
