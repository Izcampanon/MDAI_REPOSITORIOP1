# ENTRADA XPRESS - Sistema de Gestión de Entradas y Recomendaciones

##  ENTREGA 3

Esta versión representa una implementación completa del sistema, enfocándose en la **funcionalidad (Services y Controllers)** y el **diseño estético (CSS y HTML)** de todas las páginas para ofrecer una experiencia de usuario optimizada y coherente.

---

##  Novedades Destacadas

###  Sistema de Recomendación Inteligente (Google GenAI)

Hemos integrado la **API Google GenAI** para ofrecer recomendaciones de eventos personalizadas.

* **Funcionalidad:** El usuario puede introducir una palabra clave (ej: **"rock"**) en el panel de recomendaciones de la página principal.
* **Resultado:** La Inteligencia Artificial analizará los eventos disponibles y sugerirá los que mejor se ajusten a la preferencia introducida.

###  Diseño y Estructura

Se han implementado todos los **Services y Controllers** necesarios para el funcionamiento de la lógica de negocio. Además, se ha completado el **diseño CSS y HTML** de cada página, asegurando una interfaz visualmente atractiva y fácil de usar.

---

##  Configuración e Inicio Rápido

###  Datos Iniciales

Para poder probar todas las funcionalidades y casos de uso, la aplicación incluye un **script de datos** que precarga usuarios, ubicaciones, locales y eventos.

###  Acceso al Modo Administrador

Para acceder al panel de administración y gestionar ubicaciones, locales y eventos:

| Rol | Correo | Contraseña |
| :--- | :--- | :--- |
| **Administrador** | `admin@example.com` | `adminpass` |

###  Iniciar la Aplicación

1.  Abre la terminal dentro del directorio raíz del proyecto.
2.  Ejecuta el siguiente comando:

    ```bash
    .\mvnw.cmd spring-boot:run
    ```

###  Acceso Web

La aplicación se inicia por defecto en el puerto **8081**. Para acceder a la página principal, introduce la siguiente URL en tu navegador:

---

##  Caso de Uso: Compra y Gestión de Entrada

Sigue estos pasos para probar el flujo completo de compra de una entrada, gestión de saldo y uso de la recomendación:

1.  **Login:** Pulsa en **"Login"**.
2.  **Registro (Opcional):** Si es la primera vez que usas la aplicación, pulsa **"Regístrate"** y rellena los datos que se te piden.
3.  **Acceso:** Inicia sesión con tus credenciales.
4.  **Comprar Entrada:** Pulsa en **"Compra Entradas"** en el menú superior.
5.  **Buscar Ubicación:** Utiliza el nombre de ubicación **"Centro"**.
6.  **Selección de Local:** Selecciona el local que prefieras.
7.  **Selección de Evento:** Selecciona el evento deseado.
8.  **Rellenar Datos:** Rellena los datos de la entrada (tipo y consumiciones).
9.  **Continuar:** Pulsa **"Confirmar y continuar a pago"**.
10. **Pago:** Pulsa **"Confirmar y pagar"** (el pago se realiza con el saldo precargado).
11. **Ver Entrada:** Una vez completada la compra, pulsa **"Ver Entrada"**.
12. **Devolución:** Desde la vista de la entrada, puedes **devolverla** para verificar que el saldo consumido se reintegra a tu cuenta.
13. **Añadir Saldo:** Puedes añadir más saldo a tu cuenta pulsando en **"Saldo"** en el menú principal.
14. **Recomendación IA:** Vuelve a la página principal y utiliza el *sidebar* de **Recomendaciones** para probar la funcionalidad GenAI introduciendo una palabra clave (ej: **"rock"**, **"jazz"**).