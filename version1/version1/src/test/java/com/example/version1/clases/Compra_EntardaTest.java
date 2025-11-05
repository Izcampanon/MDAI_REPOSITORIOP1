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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

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


    //test que compreuebe que el usuario puede comprar una entrada para un evento
    //Al comprar la entrada el suuario debe seleccionar cantidad, tipo, y debeintroducir el nombre de los asistentes conforme la
    //cantidad de entradas seleccionadas
    /*@Test
    void testComprarEntrada() {

    }*/

    //tets que compruebe que se han introducidido al menos un nombre
    //y que lo muestre (lista)


}
