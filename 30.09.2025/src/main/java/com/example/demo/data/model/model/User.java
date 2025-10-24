package com.example.demo.data.model.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users") // Cambia el nombre de la tabla para evitar conflicto con palabra reservada
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;


    private String name;

    private String email;

    private String categProfesional;

    public User(){

    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public User(String name, String email, String categProfesional) {
        this.name = name;
        this.email = email;
        this.categProfesional = categProfesional;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCategProfesional() {
        return categProfesional;
    }

    public void setCategProfesional(String categProfesional) {
        this.categProfesional = categProfesional;
    }
}
