package com.example.demo.data.model.repository;

import com.example.demo.data.model.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository <User, Long> {
    User findByUsername(String username);

    Iterable<User> findByAllByNameStartingWith(String prefix);

    Iterable<User> findByCategProfesionalAndEmail(String categProfesional, String email);

}
