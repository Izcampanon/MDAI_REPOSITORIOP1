# ✨ ENTRADAXPRESS

---

### LOGO
![Logo de ENTRADAXPRESS](https://i.pinimg.com/736x/7e/4d/13/7e4d13abe172e3c7dc602defb289e2ca.jpg)

---

### INTEGRANTES
* Izan Campanon Lopez //DNI: 80103976J
  ![Foto izan](https://i.pinimg.com/736x/9b/02/8f/9b028fd13e2c1b80e0820d3e30edef79.jpg)
* Lucia Gil Corrales //DNI: 77027423D
  ![Foto lucia](https://i.pinimg.com/736x/41/9f/38/419f38a771b9b47d229690f85b6b2f23.jpg)

---

### ESLOGAN
La fiesta no espera, asegura tu entrada hoy HOLA IZAN 

---

### RESUMEN
EntradasXpress permite comprar entradas para fiestas de manera rápida y segura, consultar toda la información del evento y recibir notificaciones. Genera entradas digitales listas para mostrar en el acceso, ofreciendo **comodidad y organización desde la compra hasta la asistencia.

#### DESCRIPCION
La aplicación permite a los usuarios comprar entradas de manera rápida y segura para fiestas y eventos. Ofrece una interfaz intuitiva donde los asistentes pueden consultar fechas, horarios, ubicación y tipos de entradas disponibles. Además, permite el pago en línea mediante diversos métodos y genera un código de entrada digital que se puede mostrar en el acceso al evento. La app también notifica a los usuarios sobre promociones, cambios de horario o eventos relacionados, asegurando una experiencia cómoda y organizada desde la compra hasta la asistencia a la fiesta.

---

## 🛠 FUNCIONALIDADES Y REQUISITOS

### Funcionalidades del Usuario

* El usuario puede registrarse (CREATE) introduciendo nombre, email, contraseña, edad, teléfono.
* El usuario inicia sesión.
* Usuario introduce nombre de ubicación.
* Usuario puede elegir local de lista de locales (según la ubicación elegida).
* Usuario puede elegir evento de lista de eventos que pertenecen al local elegido.
* Usuario puede ver detalles del evento (READ): título, fecha, artista, local y descripción.
* Usuario puede comprar entradas para el evento (READ).
* Usuario al comprar entrada introduce nombre/s de los asistente/s.
* Usuario procesa pago (simulado).
* Usuario obtiene entrada.
* Usuario puede ver sus entradas compradas (READ).
* Usuario puede cancelar una entrada (DELETE).
* Cada vez que el usuario quiera realizar una compra deberá seleccionar la ubicación.

### Funcionalidades del Sistema

* El sistema puede borrar usuario (DELETE).
* El sistema puede actualizar datos de usuario (UPDATE).
* El sistema crea la entrada cuando el usuario la compra (CREATE).
* El sistema puede eliminar entrada si el usuario la cancela (DELETE).
* El sistema crea el usuario cuando se registra (CREATE).
* El sistema lista locales por ubicación (READ).
* El sistema lista eventos por local (READ).
* El sistema comprueba que exista la ubicación introducida por el usuario.
* El sistema comprueba si el usuario es mayor o menor de edad; si es menor, **todos los eventos aparecerán no disponibles.
* Cuando el usuario compra la entrada se crea un registro de la compra (compra_entarda) que indica la fecha de compra, el usuario, el precio, el evento, y los nombres de los asistentes.

### Reglas de Estado del Evento y Entradas

* El evento posee un estado (disponible, no disponible) que condiciona que aparezca o no en la lista de eventos.
* El evento posee un aforo (Máximo). Si el aforo llega al máximo, se **cambia el estado del evento a no disponible.
* Cuando la fecha del evento ha pasado, se **cambia el estado del evento a no disponible.

> Resumen de Disponibilidad: El evento solo aparecerá al usuario cuando su estado sea disponible. Esto ocurre si:
> 1.  El aforo no es completo.
> 2.  La fecha del evento no ha pasado.
> 3.  La Edad del usuario es adecuada para el evento.

* Cada entrada tendrá o no una cantidad de consumiciones.