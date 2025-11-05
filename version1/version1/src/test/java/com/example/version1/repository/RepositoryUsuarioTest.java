// RepositoryUsuarioTest.java
package com.example.version1.repository;

import com.example.version1.model.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class RepositoryUsuarioTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RepositoryUsuario usuarioRepository;
}