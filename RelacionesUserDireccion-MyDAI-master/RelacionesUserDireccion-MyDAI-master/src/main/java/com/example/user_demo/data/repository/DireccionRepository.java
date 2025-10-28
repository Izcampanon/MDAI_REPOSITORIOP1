package com.example.user_demo.data.repository;

import com.example.user_demo.data.model.Direccion;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DireccionRepository extends CrudRepository<Direccion, Long> {
    public Direccion findByName(String name);
}
