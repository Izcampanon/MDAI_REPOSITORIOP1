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
        Ubicacion ubicacion= new Ubicacion(null, "Merida");

        //Verificamos los atributos
        assertNull(ubicacion.getId());
        assertEquals("Merida", ubicacion.getNombre());

        //Persistir entidad y verificar id generado
        ubicacion=ubicacionRepository.save(ubicacion);

        //Verificamos que el id no es nulo
        assertNotNull(ubicacion.getId());

        ubicacion.setNombre("Badajoz");
        assertEquals("Badajoz", ubicacion.getNombre());

        //Guardamos el cambio
        ubicacionRepository.save(ubicacion);

        //Recuperamos para ver si se guardo correctamente
        Ubicacion ubicacionRecuperada=ubicacionRepository.findById(ubicacion.getId()).orElse(null);
        assertNotNull(ubicacionRecuperada);
        assertEquals("Badajoz", ubicacionRecuperada.getNombre());

    }


    @Test
    void obtenerUsuariosPorUbicacion() {
        // 1. Crear y guardar la Ubicacion de referencia
        Ubicacion ubicacion = new Ubicacion(null, "Bilbao");
        ubicacion = ubicacionRepository.save(ubicacion);

        // 2. Crear y asociar Usuario 1 a "Bilbao" usando el helper
        Usuario usuario1 = new Usuario("Iker", "iker@test.com", "pass1", true, "111");
        ubicacion.addUsuario(usuario1);
        usuarioRepository.save(usuario1);

        // 3. Crear y asociar Usuario 2 a "Bilbao" usando el helper
        Usuario usuario2 = new Usuario("Leire", "leire@test.com", "pass2", true, "222");
        ubicacion.addUsuario(usuario2);
        usuarioRepository.save(usuario2);

        // 4. Crear un Usuario en otra Ubicacion (para asegurar el aislamiento)
        Ubicacion otraUbicacion = new Ubicacion(null, "Donostia");
        otraUbicacion = ubicacionRepository.save(otraUbicacion);
        Usuario usuario3 = new Usuario("Aitor", "aitor@test.com", "pass3", true, "333");
        otraUbicacion.addUsuario(usuario3);
        usuarioRepository.save(usuario3);

        // 5. Recuperar la Ubicacion principal
        Ubicacion ubicacionRecuperada = ubicacionRepository.findById(ubicacion.getId()).orElse(null);
        assertNotNull(ubicacionRecuperada, "La Ubicacion 'Bilbao' debe ser recuperada.");

        // 6. Verificar la lista de Usuarios asociados
        List<Usuario> usuarios = ubicacionRecuperada.getUsuarios();

        assertNotNull(usuarios, "La lista de usuarios no debe ser null.");
        assertEquals(2, usuarios.size(), "Debe haber 2 usuarios asociados a 'Bilbao'.");

        // 7. Verificar que los nombres de los usuarios sean correctos
        assertTrue(usuarios.stream().anyMatch(u -> u.getNombre().equals("Iker")));
        assertTrue(usuarios.stream().anyMatch(u -> u.getNombre().equals("Leire")));
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
        Usuario usuario = new Usuario("Pepito", "pepito@test.com", "pass", true, "000");
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

    @Test
    void comprobarLocalesinicialesUbicacion() {
        // Crear y guardar una Ubicacion sin locales
        Ubicacion ubicacion = new Ubicacion(null, "Valencia");
        ubicacion = ubicacionRepository.save(ubicacion);

        // Recuperar la Ubicacion y verificar que la lista de locales está vacía
        Ubicacion ubicacionRecuperada = ubicacionRepository.findById(ubicacion.getId()).orElse(null);
        assertNotNull(ubicacionRecuperada, "La ubicacion debe ser recuperada");
        List<Local> locales = ubicacionRecuperada.getLocales();
        assertNotNull(locales, "La lista de locales no debe ser null");
        assertTrue(locales.isEmpty(), "La lista de locales debe estar vacía inicialmente");
    }

}
