## 3- ms-catalog (`ms-catalog`)
# Microservicio de Catálogo e Inventario (`ms-catalog`)

Microservicio encargado de la gestión del catálogo de productos y el control transaccional del stock e inventario.

##  REQUISITO CRÍTICO DE DESPLIEGUE

Para que este microservicio funcione correctamente con la nueva arquitectura de mensajería asíncrona:

1. **Cambio de Rama Obligatorio:**
   Debes cambiarte a la rama `rabbitmq` antes de levantar el contenedor:
   git checkout rabbitmq
Orden de Encendido:
OBLIGATORIAMENTE se debe levantar primero el microservicio ms-orders antes de iniciar este microservicio, ya que ms-orders inicializa la infraestructura central de mensajería y la red compartida.

Inicio del Servicio
Una vez que ms-orders esté corriendo y estés parado en la rama rabbitmq:

Bash
docker compose up -d --build
Integración con RabbitMQ
Este microservicio interactúa con el clúster de RabbitMQ de la siguiente manera:

Consumidor de Comandos de Stock:

Escucha la cola: catalog.stock.cmd.queue (vinculada a catalog.direct.exchange).

Función: Procesa la solicitud de descuento de inventario de forma asíncrona.

Resiliencia y Manejo de Errores (ACK/NACK y DLQ):

Si el descuento en base de datos es exitoso, emite un ACK explícito/automático.

Si ocurre un fallo, cuenta con 3 reintentos acotados (max-attempts: 3).

Si los reintentos se agotan, emite un NACK con requeue=false, derivando el mensaje fallido a la Dead Letter Queue (catalog.stock.dlq) a través de catalog.dlx.exchange.

Productor de Notificaciones de Stock:

Tras actualizar el stock con éxito, publica un mensaje en catalog.direct.exchange con Routing Key catalog.stock.updated.key para notificar de vuelta a ms-orders.
