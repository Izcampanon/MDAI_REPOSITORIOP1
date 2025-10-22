package com.example.version1.repository;

import com.example.version1.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepositoryUsuario extends JpaRepository<Usuario, Long> {

}
