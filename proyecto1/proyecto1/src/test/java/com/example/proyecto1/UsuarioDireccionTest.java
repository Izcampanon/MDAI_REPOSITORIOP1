package com.example.proyecto1;

import com.example.proyecto1.data.model.Direccion;
import com.example.proyecto1.data.model.Usuario;
import com.example.proyecto1.data.repository.DireccionRepository;
import com.example.proyecto1.data.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

@SpringBootTest
class UsuarioDireccionTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DireccionRepository direccionRepository;

    @Test
    void testRelacionUsuarioDireccion() {
        Usuario usuario = new Usuario("Ana", "ana@email.com");

        Direccion dir1 = new Direccion();
        dir1.setDir("Calle 1");
        dir1.setCiudad("Ciudad X");
        dir1.setUsuario(usuario);

        Direccion dir2 = new Direccion();
        dir2.setDir("Calle 2");
        dir2.setCiudad("Ciudad Y");
        dir2.setUsuario(usuario);

        usuario.getDirecciones().add(dir1);
        usuario.getDirecciones().add(dir2);

        usuario = userRepository.save(usuario);

        Usuario usuarioRecuperado = userRepository.findById(usuario.getId()).orElse(null);
        assertNotNull(usuarioRecuperado);
        List<Direccion> direcciones = usuarioRecuperado.getDirecciones();
        assertEquals(2, direcciones.size());
    }
}
