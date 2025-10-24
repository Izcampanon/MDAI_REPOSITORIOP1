package com.example.proyecto1.data.repository;

import com.example.proyecto1.data.model.Usuario;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository  extends CrudRepository<Usuario,Integer> {
}
