package com.example.proyecto2;

import com.example.proyecto2.model.*;
import com.example.proyecto2.repository.*;
import com.example.proyecto2.model.Book;
import com.example.proyecto2.model.Publisher;
import com.example.proyecto2.model.Review;
import com.example.proyecto2.repository.BookRepository;
import com.example.proyecto2.repository.PublisherRepository;
import com.example.proyecto2.repository.ReviewRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class Proyecto2ApplicationTests {

    @Autowired
    private PublisherRepository publisherRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    void testCascadePersistFromPublisherToBook() {
        Publisher publisher = new Publisher("Editorial A");
        Book book = new Book("Spring Boot Guide", "ISBN-001");
        publisher.addBook(book); // mantiene ambas caras

        publisherRepository.save(publisher);
        em.flush();

        assertEquals(1, bookRepository.count(), "El Book debe haberse persistido por cascade PERSIST");
        Book saved = bookRepository.findAll().get(0);
        assertEquals("Spring Boot Guide", saved.getTitle());
        assertNotNull(saved.getPublisher());
    }

    @Test
    void testCascadeRemoveFromBookToReview() {
        Book book = new Book("Clean Architecture", "ISBN-002");
        Review r1 = new Review("Excelente", 5);
        Review r2 = new Review("Muy bueno", 4);

        book.addReview(r1);
        book.addReview(r2);

        bookRepository.save(book);
        em.flush();

        bookRepository.deleteById(book.getId());
        em.flush();

        assertEquals(0, reviewRepository.count(), "Las reviews deben eliminarse cuando se borra el book (cascade + orphanRemoval)");
    }

    @Test
    void testNoCascadeRemoveFromPublisherToBook() {
        Publisher publisher = new Publisher("Editorial B");
        Book book = new Book("Domain Driven Design", "ISBN-003");
        publisher.addBook(book);

        publisherRepository.save(publisher);
        em.flush();

        try {
            publisherRepository.deleteById(publisher.getId());
            em.flush();
        } catch (Exception ex) {
            // Puede lanzarse una excepción por FK si la DB no permite borrar el publisher mientras existan libros.
            // Eso no rompe el objetivo de la prueba: confirmar que NO se borraron los books por cascade desde Publisher.
        }

        assertTrue(bookRepository.existsById(book.getId()), "El Book debe seguir existiendo tras intentar borrar el Publisher (no cascade remove).");
    }
}

