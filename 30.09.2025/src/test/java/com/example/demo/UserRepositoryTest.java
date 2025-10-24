package com.example.demo;

import com.example.demo.data.model.model.User;
import com.example.demo.data.model.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

     /* void findUserName(){
         //userRepository.deleteAll(); // Limpiar la base de datos antes del test
         User user = new User("John", "john@alumnos.unex.es"); // usuario creado
         System.out.println(user.getId());
         userRepository.save(user);
         System.out.println(user.getId());
         User savedUser = userRepository.save(user); // usuario guardado en la base de datos
         User fetchedUser = userRepository.findById(savedUser.getId()).orElse(null); // usuario recuperado de la base de datos

         Assertions.assertNotNull(fetchedUser); // comprobamos que el usuario no es nulo
         Assertions.assertEquals("John", fetchedUser.getName()); // comprobamos el nombre
         Assertions.assertEquals("john@alumnos.unex.es", fetchedUser.getEmail()); // comprobamos el email

         /*
        User user = new User("John Doe", "aaa@alumnos.es");
        userRepo.save(user);
        Long userId = user.getId();
        User fetchedUser = userRepo.findById(userId).orElse(null);
        org.junit.jupiter.api.Assertions.assertNotNull(fetchedUser);
        org.junit.jupiter.api.Assertions.assertEquals(userId, fetchedUser.getId());


     }*/

    /* void testFindByAllByNameStartingWith() {
         Iterable<User> users = userRepository.findByAllByNameStartingWith("J");
         int count = 0;
            for (User user : users) {
                count++;
            }
            Assertions.assertEquals(2, count); // Asegurarse de que se encontraron usuarios
     }*/

    @Test
    public void test1() {
        userRepository.deleteAll();
        User user = new User("TestName", "test@email.com", "TestCategoria");
        userRepository.save(user);
        Iterable<User> result = userRepository.findByCategProfesionalAndEmail("TestCategoria", "test@email.com");
        User found = result.iterator().hasNext() ? result.iterator().next() : null;
        Assertions.assertNotNull(found);
        Assertions.assertEquals("TestName", found.getName());
        Assertions.assertEquals("TestCategoria", found.getCategProfesional());
        Assertions.assertEquals("test@email.com", found.getEmail());
    }


}
