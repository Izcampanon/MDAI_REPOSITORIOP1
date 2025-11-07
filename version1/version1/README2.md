# APLICACION DE VENTAS DE ENTRADAS

>  API REST para la gestión de eventos y compra de entradas.

---

Este proyecto está construido utilizando:

* *Java* (Versión 21 )
* *Spring Boot*
* *Spring Web* (Para construir la API REST)
* *Spring Data JPA* (Para el mapeo Objeto-Relacional)
* *H2 Database* (Como base de datos en memoria para desarrollo y pruebas)
* *Spr* (Para la gestión de dependencias y construcción del proyecto)

---

## 💾 Base de Datos: H2

Para facilitar el desarrollo y las pruebas, el proyecto está configurado para conectarse a una base de datos *H2 en memoria*.

* *No requiere instalación:* La base de datos se crea y se destruye automáticamente cada vez que se inicia y se detiene la aplicación.
* *Consola H2:* Puedes acceder a la consola web de H2 para inspeccionar la base de datos mientras la aplicación está en ejecución.
    * *URL:* http://localhost:8080/h2-console
    * *JDBC URL:* (Revisa tu application.properties, pero usualmente es jdbc:h2:mem:testdb)
    * *Usuario:* sa
    * *Contraseña:* (Revisa tu application.properties o déjalo en blanco si no has puesto)

---

## 🏗 Arquitectura y Diseño

A continuación se detallan los diagramas que definen la estructura de la base de datos y la lógica de la aplicación.

### 1. Diagrama de Entidad-Relación (D-E)

El D-E muestra la estructura de la base de datos, las tablas y cómo se relacionan entre sí.


![Diagrama de Entidad-Relación](https://i.pinimg.com/736x/16/46/58/164658dd6d62f09703ca3a458d9c43ee.jpg)

### 2. Diagrama de Clases (UML)

El diagrama de clases UML muestra las entidades (@Entity) y sus relaciones, atributos y métodos, representando la arquitectura del dominio de la aplicación.


![Diagrama de Clases](https://i.pinimg.com/736x/09/40/26/094026f31a07fa2771a57c096aeb17a5.jpg)