# Caso real: Segment, "Goodbye Microservices" (2018)

## 1. Contexto

Segment recibe eventos de sus clientes y los reenvía a decenas de herramientas de terceros
("destinos"). Al principio todo pasaba por una sola cola; cuando un destino respondía lento, sus
reintentos retrasaban la entrega a todos los demás (bloqueo de cabeza de línea). Para aislarlos,
le dieron a cada destino su propio servicio y su propia cola, y después su propio repositorio.
Llegaron a más de 140 servicios, con unos tres destinos nuevos por mes, mantenidos por un equipo
pequeño: el artículo cuenta que había "3 full-time engineers spending most of their time just
keeping the system alive".

## 2. Decisión tomada

Volvieron atrás: juntaron el código de todos los destinos en un solo repositorio, con una sola
versión de cada una de sus 120 dependencias, y unieron los más de 140 servicios en un único
servicio. Construyeron Centrifuge para reemplazar las colas por destino y Traffic Recorder para que
las pruebas no dependieran de llamadas HTTP reales, que fallaban con frecuencia.

## 3. Resultado

- Las pruebas de los más de 140 destinos pasaron a correr en milisegundos; antes, las de un solo
  destino podían tardar minutos.
- Las mejoras a las librerías compartidas pasaron de 32 en el año de microservicios a 46 el año
  siguiente, porque ya no había que actualizar cada repositorio por separado.
- Un solo ingeniero puede desplegar en minutos, en lugar de desplegar más de 140 servicios.
- Lo que perdieron, según el propio artículo: el aislamiento de fallos (un destino que se cae puede
  tumbar todo el servicio) y parte de la eficiencia del caché en memoria.

## 4. Qué tendría que ser cierto en nuestro proyecto para que la lección aplique

La lección de Segment es que muchos servicios casi idénticos, mantenidos por pocas personas,
cuestan más de lo que aíslan. Para que nos aplicara, tendríamos que tener muchos servicios con la
misma forma (como sus destinos), que la mayor parte del tiempo del equipo se fuera en mantener la
infraestructura y no en funcionalidad, y que los servicios compartieran librerías que se
desincronizan.

## 5. ¿Se aplica? Parcialmente

No tenemos 140 servicios: tenemos dos (tres cuando se extraiga sensores), y cada uno tiene un
dominio distinto, con datos propios y un ritmo de cambio propio. Ahí la separación sí paga: Alertas
cambia sus reglas sin tocar Envíos. Pero sí aplica como advertencia, y por eso descartamos la opción
B de nuestra matriz (separar también ingesta, notificaciones y consultas): con tres personas
terminaríamos como Segment, manteniendo infraestructura en lugar del producto. También tomamos su
lección sobre las pruebas: nuestras pruebas del adaptador HTTP usan un servidor simulado
(`MockRestServiceServer`) en lugar de llamar a `alertas-service` real, por la misma razón que ellos
crearon Traffic Recorder.

## Referencia

[1] A. Noonan, "Goodbye Microservices: From 100s of problem children to 1 superstar," *Segment
Engineering Blog* (Twilio), Jul. 10, 2018. [Online]. Available:
https://www.twilio.com/en-us/blog/developers/best-practices/goodbye-microservices
