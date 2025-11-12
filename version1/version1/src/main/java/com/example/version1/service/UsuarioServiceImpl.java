package com.example.version1.service;


import com.example.version1.repository.RepositoryUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final RepositoryUsuario repositoryUsuario;

    @Autowired
    public UsuarioServiceImpl(RepositoryUsuario repositoryUsuario) {
        System.out.println("\t Constructing UsuarioServiceImpl");
        this.repositoryUsuario = repositoryUsuario;
    }

}
