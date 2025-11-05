FUNCIONALIDADES Y REQUISITOS

-El usuario puede registrarse (CREATE) introduciendo nombre, email, contraseña, edad, telefono.
-El usuario inicia sesion.
-Usuario introuduce nombre de ubicacion.
-Usuario puede elegir local de lista de locales (segun la ubicaicon elegida).
-Usuario puede elegir evento de lista de eventos que pertenecen al local elegido.
-Usuario puede ver detalles del evento (titulo, fecha, artista, local y descripcion).
-Usuario puede comprar entradas para el evento (READ).
-Usuario al comprar entrada introduce nombre/s de los asistente/s.
-Usuario procesa pago (simulado).
-Usuario obtiene entrada.
-Usuario puede ver sus entradas compradas (READ).
-Usuario puede cancelar una entrada (DELETE).


-El sistema puede borrar usuario (DELETE).
-El sistema puede actualizar datos de usuario (UPDATE). 
-El sistema crea la entrada cuando el usauraio la compra (CREATE).
-El sistema puede eliminar entarda si el usuario la cancela (DELETE).
-El sistema crea el usuario cuando se registra (CREATE).
-El sistema lista locales por ubicacion (READ). 
-El sistema lista eventos por local (READ).
-El sistema comprueba que exista la ubicacion introducida por el usuario.
-El sistema comprueba si el usuario es mayor o menor de edad, si es menor todos los eventos apareceran no disponibles.

-Cuando el usuario compra la entarda se crea un registro de la compra (compra_entarda) que indica la fecha de compra, el usuario, el precio, el evento, los nombres de los asistentes.



-Evento posee un estado (dispobibe, no disponible). Que conduciona que aparezca o no en la lista de eventos que se muestra al usuario.
-Evento posee un aforo (Maximo). Si el aforo llega al maximo se cambia el estado del evento a no disponible.
-Cuando la fecha del evento ha pasado se cambia el estado del evento a no disponible, mientras tanto aparecera como disponible.
-Resumen: el evento solo parecera al usuario cuando su estado sea disponible. ¿cuando es disponible?
    -El aforo no es completo.
    -La fecha del evento no ha pasado.
    -Edad del usuario es adecuada para el evento.


-Cada entrada tendra o no una canrtidad de consumiciones.

-Cada vez que el usuario quiera realizar una compra debera seleccionar la ubicacion.


