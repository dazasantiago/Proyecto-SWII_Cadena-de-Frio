# Retrospectiva — Reynel

## ¿Qué salió bien?

El dominio de sensores ya tenía piezas pequeñas para validar lecturas. Separarlas en un servicio permitió conservar esa lógica y definir un evento para integrar sensores con alertas mediante Kafka.

## ¿Qué fue difícil?

El servicio necesita conocer la condición de temperatura y humedad de cada envío. En esta entrega la API recibe esos límites junto con la lectura; todavía falta integrar una consulta entre servicios para obtenerlos desde Envíos.

## ¿Qué aprendí?

Al extraer un dominio a un microservicio, también hay que explicitar los datos que antes llegaban por dependencias internas. El evento debe incluir el contador de lecturas consecutivas para que Alertas pueda determinar la severidad.

## ¿Qué mejoraría?

Persistiría el contador por sensor y envío para que no se reinicie al reiniciar el proceso, y acordaría con el equipo un contrato versionado para la consulta de condiciones y el evento Kafka.
