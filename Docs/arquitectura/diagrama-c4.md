---
grupo: G6
proyecto: Cadena de frío / logística con sensores IoT
tipo: Diagrama C4 (Contexto + Contenedores)
fuente-requisitos: MD/Guías y Material del Docente/Requisitos_G6.md
---

# Diagrama C4 — Sistema de Cadena de Frío (G6)

Este documento describe la **arquitectura objetivo** del sistema: el diseño hacia el que convergen las entregas progresivas del curso (hexagonal, microservicios, CQRS, EDA, saga, persistencia políglota). No es una implementación de una sola entrega — es el mapa completo sobre el que cada milestone del curso construye una porción.

## Por qué esta arquitectura

Los requisitos no funcionales del proyecto apuntan directamente a **EDA + microservicios + CQRS**, con **DDD/hexagonal** dentro de cada servicio:

| Requisito no funcional | Decisión arquitectónica |
|---|---|
| Alto volumen sostenido de eventos de sensores | Bus de eventos (Kafka) como columna vertebral; ingesta desacoplada del procesamiento |
| Alertas casi en tiempo real (<5 min, HU-03) | Procesamiento por stream sobre el topic de lecturas, sin batch |
| No perder lecturas aunque falle un componente | Kafka como log durable y reproducible; consumidores retoman desde su offset |
| Escalar contenedores/sensores sin rediseñar | Servicios desacoplados por eventos, escalado horizontal independiente por contenedor |
| Cierre de envío no puede tener alertas críticas sin resolver (HU-08) | Saga orquestada entre el contexto de Envíos y el de Alertas |
| Historial de lecturas vs. consultas de estado/dashboard (HU-06, HU-07) | CQRS: escritura de alto volumen separada del modelo de lectura |

## Bounded contexts

| Contexto | Entidades del dominio | Historias de usuario |
|---|---|---|
| Gestión de Envíos | Envío, Condición requerida | HU-01, HU-08, HU-09 |
| Telemetría / Ingesta | Sensor, Lectura | HU-02, HU-06, HU-07 |
| Detección de Alertas | Alerta | HU-03 |
| Notificaciones | — | HU-04, HU-05 |

> **Supuesto de diseño — rol Conductor.** `Requisitos_G6.md` lista "Conductor / transportista" como actor en la narrativa, pero ninguna de las 9 HU está escrita desde su punto de vista (todas son del Operador logístico, el Sensor IoT, el Sistema o el Cliente). Que el conductor consulte su envío asignado desde la misma **Aplicación Web** que usan operador y cliente es una inferencia razonable, no un requisito explícito — queda pendiente confirmar su alcance real con el asesor/docente antes de comprometerlo en una entrega.

## C1 — Diagrama de Contexto

```mermaid
---
title: Diagrama de Contexto - Sistema de Cadena de Frío (G6)
---
flowchart TB
    classDef person fill:#08427b,stroke:#052e56,color:#fff
    classDef system fill:#1168bd,stroke:#0b4884,color:#fff
    classDef external fill:#999999,stroke:#6b6b6b,color:#fff

    notif["<b>Gateway de Notificaciones</b><br/>[Sistema externo]<br/><br/>Servicio externo de push/email/SMS"]:::external
    operador("<b>Operador logístico</b><br/>[Persona]<br/><br/>Registra envíos, monitorea alertas, cierra envíos, reporta incidentes"):::person
    cliente("<b>Cliente</b><br/>[Persona]<br/><br/>Dueño de la carga: hace seguimiento y recibe alertas"):::person
    conductor("<b>Conductor / transportista</b><br/>[Persona]<br/><br/>Transporta el contenedor"):::person

    sensorIot["<b>Sensor IoT</b><br/>[Sistema externo]<br/><br/>Dispositivo embebido que mide temperatura y humedad del contenedor"]:::external
    sistemaCadenaFrio["<b>Sistema de Cadena de Frío</b><br/>[Sistema de software]<br/><br/>Monitorea condiciones de contenedores refrigerados y genera alertas automáticas de quiebre de cadena de frío"]:::system

    operador -->|"Registra envíos, consulta<br/>historial, cierra envíos,<br/>reporta incidentes<br/>[HTTPS]"| sistemaCadenaFrio
    cliente -->|"Consulta estado y<br/>ubicación de su carga<br/>[HTTPS]"| sistemaCadenaFrio
    conductor -->|"Consulta envío asignado<br/>[HTTPS]"| sistemaCadenaFrio
    sensorIot -->|"Envía lecturas continuas<br/>de temperatura/humedad<br/>[MQTT]"| sistemaCadenaFrio
    sistemaCadenaFrio -->|"Envía alertas<br/>para despachar<br/>[HTTPS/API]"| notif
    notif -.->|"Notifica alerta<br/>en tiempo real<br/>[Push/Email/SMS]"| operador
    notif -.->|"Notifica alerta<br/>en tiempo real<br/>[Push/Email/SMS]"| cliente
```

## C2 — Diagrama de Contenedores (vista completa)

Versión simplificada para el alcance de un proyecto universitario: **una sola aplicación de usuario** (operador, cliente y conductor comparten la misma app web — no hay app móvil separada) y **sin API Gateway como contenedor propio**: la app web llama directamente a los dos servicios con los que interactúa una persona (Envíos y Consultas). El broker MQTT tampoco es un contenedor aparte — se modela como el listener MQTT embebido del Servicio de Ingesta, que es la única pieza que le habla a los sensores. Quedan 9 contenedores: 1 de interfaz, 4 servicios de dominio, el bus de eventos y 3 bases de datos.

```mermaid
---
title: Diagrama de Contenedores - Sistema de Cadena de Frío (G6)
config:
  flowchart:
    rankSpacing: 70
    nodeSpacing: 40
---
flowchart TB
    classDef person fill:#08427b,stroke:#052e56,color:#fff
    classDef external fill:#999999,stroke:#6b6b6b,color:#fff
    classDef container fill:#438dd5,stroke:#2e6295,color:#fff
    classDef boundary fill:none,stroke:#444,stroke-width:1px,stroke-dasharray:6 4,color:#444

    operador("<b>Operador logístico</b><br/>[Persona]"):::person
    cliente("<b>Cliente</b><br/>[Persona]"):::person
    conductor("<b>Conductor / transportista</b><br/>[Persona]"):::person
    sensorIot["<b>Sensor IoT</b><br/>[Sistema externo]"]:::external
    notif["<b>Gateway de Notificaciones</b><br/>[Sistema externo]"]:::external

    subgraph sistema["Sistema de Cadena de Frío [Sistema]"]
        webApp["<b>Aplicación Web</b><br/>[Contenedor: SPA React/Angular]<br/><br/>Dashboard de envíos, alertas e historial para operador y cliente; envío asignado para el conductor"]:::container
        ingestion["<b>Servicio de Ingesta</b><br/>[Contenedor: Spring Boot + listener MQTT embebido]<br/><br/>Recibe lecturas de los sensores y las publica en el bus"]:::container
        shipmentSvc["<b>Servicio de Envíos</b><br/>[Contenedor: Spring Boot hexagonal]<br/><br/>Ciclo de vida del envío, condiciones requeridas, incidentes, saga de cierre"]:::container
        alertSvc["<b>Servicio de Detección de Alertas</b><br/>[Contenedor: Spring Boot stream processor]<br/><br/>Evalúa cada lectura contra la condición requerida y genera alertas"]:::container
        notifSvc["<b>Servicio de Notificaciones</b><br/>[Contenedor: Spring Boot]<br/><br/>Despacha alertas a operador y cliente en tiempo real"]:::container
        queryApi["<b>Servicio de Consultas</b><br/>[Contenedor: Spring Boot]<br/><br/>Lado de lectura CQRS: historial, estado actual, dashboards"]:::container
        broker["<b>Bus de Eventos</b><br/>[Contenedor: Apache Kafka]<br/><br/>LecturaRecibida, AlertaGenerada, EnvioRegistrado, EnvioCerrado"]:::container
        shipmentDb[("<b>BD Envíos</b><br/>[Contenedor: PostgreSQL]<br/><br/>Envíos, condiciones requeridas, incidentes")]:::container
        alertDb[("<b>BD Alertas</b><br/>[Contenedor: PostgreSQL]<br/><br/>Alertas y su estado de resolución")]:::container
        tsDb[("<b>BD Series de Tiempo</b><br/>[Contenedor: TimescaleDB / InfluxDB]<br/><br/>Histórico de lecturas de sensores")]:::container
    end
    class sistema boundary

    operador -->|"Usa<br/>[HTTPS]"| webApp
    cliente -->|"Usa<br/>[HTTPS]"| webApp
    conductor -->|"Usa<br/>[HTTPS]"| webApp
    sensorIot -->|"Publica lecturas<br/>[MQTT]"| ingestion

    webApp -->|"Registra/cierra envíos,<br/>reporta incidentes<br/>[HTTPS/JSON]"| shipmentSvc
    webApp -->|"Consulta historial,<br/>estado, dashboards<br/>[HTTPS/JSON]"| queryApi

    shipmentSvc -->|"Publica EnvioRegistrado /<br/>EnvioCerrado<br/>[Kafka]"| broker
    ingestion -->|"Publica LecturaRecibida<br/>[Kafka]"| broker

    broker -->|"Consume LecturaRecibida<br/>[Kafka]"| alertSvc
    alertSvc --->|"Lee condición requerida<br/>[JDBC]"| shipmentDb
    alertSvc --->|"Persiste alerta<br/>[JDBC]"| alertDb
    alertSvc -->|"Publica AlertaGenerada<br/>[Kafka]"| broker

    broker -->|"Consume AlertaGenerada<br/>[Kafka]"| notifSvc
    notifSvc -->|"Envía notificación<br/>[HTTPS/API]"| notif

    shipmentSvc --->|"Lee/escribe<br/>[JDBC]"| shipmentDb
    shipmentSvc -->|"Consulta alertas críticas sin<br/>resolver (saga de cierre)<br/>[HTTPS/REST síncrono]"| alertSvc

    broker -->|"Consume LecturaRecibida,<br/>AlertaGenerada,<br/>EnvioRegistrado/Cerrado<br/>[Kafka]"| queryApi
    queryApi --->|"Lee estado de envíos<br/>[SQL]"| shipmentDb
    queryApi --->|"Lee alertas<br/>[SQL]"| alertDb
    queryApi --->|"Materializa histórico<br/>[Write API]"| tsDb
    queryApi --->|"Lee histórico<br/>[SQL]"| tsDb
```

Aun así, Kafka concentra 6 relaciones y mezcla el flujo síncrono (persona → app web → servicio) con el asíncrono (sensor → Kafka → consumidores), así que para lectura y presentación conviene usar las dos vistas enfocadas de C2a/C2b — el mismo modelo separado por pregunta ("¿cómo interactúa una persona?" vs. "¿cómo se convierte una lectura en una alerta?").

## C2a — Vista: Flujo de negocio (síncrono)

Responde "¿cómo interactúa una persona con el sistema?": operador, cliente y conductor usan la misma app web, que llama directo a los servicios de Envíos y Consultas. Incluye la llamada síncrona de la saga de cierre (`shipmentSvc → alertSvc`).

```mermaid
---
title: Vista C2a - Flujo de negocio (síncrono)
config:
  flowchart:
    rankSpacing: 70
    nodeSpacing: 40
---
flowchart TB
    classDef person fill:#08427b,stroke:#052e56,color:#fff
    classDef container fill:#438dd5,stroke:#2e6295,color:#fff
    classDef boundary fill:none,stroke:#444,stroke-width:1px,stroke-dasharray:6 4,color:#444

    operador("<b>Operador logístico</b><br/>[Persona]"):::person
    cliente("<b>Cliente</b><br/>[Persona]"):::person
    conductor("<b>Conductor / transportista</b><br/>[Persona]"):::person

    subgraph sistema["Sistema de Cadena de Frío [Sistema]"]
        webApp["<b>Aplicación Web</b><br/>[Contenedor: SPA React/Angular]<br/><br/>Dashboard de envíos, alertas e historial; envío asignado para el conductor"]:::container
        shipmentSvc["<b>Servicio de Envíos</b><br/>[Contenedor: Spring Boot hexagonal]<br/><br/>Ciclo de vida del envío, saga de cierre"]:::container
        alertSvc["<b>Servicio de Detección de Alertas</b><br/>[Contenedor: Spring Boot stream processor]<br/><br/>Consultado por la saga de cierre"]:::container
        queryApi["<b>Servicio de Consultas</b><br/>[Contenedor: Spring Boot]<br/><br/>Lado de lectura CQRS"]:::container
        shipmentDb[("<b>BD Envíos</b><br/>[Contenedor: PostgreSQL]<br/><br/>Envíos, condiciones requeridas, incidentes")]:::container
        alertDb[("<b>BD Alertas</b><br/>[Contenedor: PostgreSQL]<br/><br/>Alertas y su estado de resolución")]:::container
        tsDb[("<b>BD Series de Tiempo</b><br/>[Contenedor: TimescaleDB / InfluxDB]<br/><br/>Histórico de lecturas de sensores")]:::container
    end
    class sistema boundary

    operador -->|"Usa<br/>[HTTPS]"| webApp
    cliente -->|"Usa<br/>[HTTPS]"| webApp
    conductor -->|"Usa<br/>[HTTPS]"| webApp

    webApp -->|"Registra/cierra envíos,<br/>reporta incidentes<br/>[HTTPS/JSON]"| shipmentSvc
    webApp -->|"Consulta historial,<br/>estado, dashboards<br/>[HTTPS/JSON]"| queryApi

    shipmentSvc -->|"Consulta alertas críticas sin<br/>resolver (saga de cierre)<br/>[HTTPS/REST síncrono]"| alertSvc
    shipmentSvc --->|"Lee/escribe<br/>[JDBC]"| shipmentDb

    queryApi --->|"Lee estado de envíos<br/>[SQL]"| shipmentDb
    queryApi --->|"Lee alertas<br/>[SQL]"| alertDb
    queryApi --->|"Lee histórico<br/>[SQL]"| tsDb
```

## C2b — Vista: Flujo de telemetría y alertas (event-driven)

Responde "¿cómo se convierte una lectura de sensor en una alerta notificada?": desde el sensor hasta la notificación, todo mediado por el bus de eventos. El sensor le habla directo al Servicio de Ingesta por MQTT (sin broker MQTT como contenedor aparte).

```mermaid
---
title: Vista C2b - Flujo de telemetría y alertas (event-driven)
config:
  flowchart:
    rankSpacing: 70
    nodeSpacing: 40
---
flowchart TB
    classDef external fill:#999999,stroke:#6b6b6b,color:#fff
    classDef container fill:#438dd5,stroke:#2e6295,color:#fff
    classDef boundary fill:none,stroke:#444,stroke-width:1px,stroke-dasharray:6 4,color:#444

    sensorIot["<b>Sensor IoT</b><br/>[Sistema externo]"]:::external
    notif["<b>Gateway de Notificaciones</b><br/>[Sistema externo]"]:::external

    subgraph sistema["Sistema de Cadena de Frío [Sistema]"]
        ingestion["<b>Servicio de Ingesta</b><br/>[Contenedor: Spring Boot + listener MQTT embebido]<br/><br/>Valida y publica lecturas"]:::container
        shipmentSvc["<b>Servicio de Envíos</b><br/>[Contenedor: Spring Boot hexagonal]<br/><br/>Publica EnvioRegistrado/Cerrado"]:::container
        broker["<b>Bus de Eventos</b><br/>[Contenedor: Apache Kafka]<br/><br/>LecturaRecibida, AlertaGenerada, EnvioRegistrado, EnvioCerrado"]:::container
        alertSvc["<b>Servicio de Detección de Alertas</b><br/>[Contenedor: Spring Boot stream processor]<br/><br/>Evalúa lecturas y genera alertas"]:::container
        notifSvc["<b>Servicio de Notificaciones</b><br/>[Contenedor: Spring Boot]<br/><br/>Despacha alertas en tiempo real"]:::container
        queryApi["<b>Servicio de Consultas</b><br/>[Contenedor: Spring Boot]<br/><br/>Materializa vistas de lectura"]:::container
        shipmentDb[("<b>BD Envíos</b><br/>[Contenedor: PostgreSQL]<br/><br/>Condición requerida del envío")]:::container
        alertDb[("<b>BD Alertas</b><br/>[Contenedor: PostgreSQL]<br/><br/>Alertas y su estado de resolución")]:::container
        tsDb[("<b>BD Series de Tiempo</b><br/>[Contenedor: TimescaleDB / InfluxDB]<br/><br/>Histórico de lecturas de sensores")]:::container
    end
    class sistema boundary

    sensorIot -->|"Publica lecturas<br/>[MQTT]"| ingestion
    shipmentSvc -->|"Publica EnvioRegistrado /<br/>EnvioCerrado<br/>[Kafka]"| broker
    ingestion -->|"Publica LecturaRecibida<br/>[Kafka]"| broker

    broker -->|"Consume LecturaRecibida<br/>[Kafka]"| alertSvc
    alertSvc --->|"Lee condición requerida<br/>[JDBC]"| shipmentDb
    alertSvc --->|"Persiste alerta<br/>[JDBC]"| alertDb
    alertSvc -->|"Publica AlertaGenerada<br/>[Kafka]"| broker

    broker -->|"Consume AlertaGenerada<br/>[Kafka]"| notifSvc
    notifSvc -->|"Envía notificación<br/>[HTTPS/API]"| notif

    broker -->|"Consume LecturaRecibida,<br/>AlertaGenerada,<br/>EnvioRegistrado/Cerrado<br/>[Kafka]"| queryApi
    queryApi --->|"Materializa histórico<br/>[Write API]"| tsDb
```

## Notas de renderizado

- Los diagramas están escritos como `flowchart` de Mermaid con estilo C4 (colores estándar de C4, tipo de elemento entre corchetes — `[Persona]`, `[Sistema externo]`, `[Contenedor: tecnología]` —, bases de datos como cilindros y la frontera del sistema como recuadro punteado). No se usa la sintaxis nativa `C4Context`/`C4Container` porque su motor de layout es experimental y coloca las etiquetas de las relaciones en el punto medio de cada flecha sin evitar colisiones: con muchas relaciones los textos quedaban encimados sobre cajas y sobre otras etiquetas. El motor de `flowchart` reserva espacio para cada etiqueta, así que no se solapan.
- Se renderizan en GitHub, VS Code (*Markdown Preview Mermaid Support* o *Mermaid Chart*) y [mermaid.live](https://mermaid.live) sin plugins adicionales.
- Si el destino final es un documento entregable (Word/PDF), exportar cada diagrama como imagen (p. ej. con [mermaid.live](https://mermaid.live)) y referenciarla en el `.md`, en vez de depender del render del visor.
- Detalles de layout que conviene conservar al editar: (a) en C1 el Gateway de Notificaciones se declara primero para que quede arriba y las personas no se desplacen hacia abajo por el ciclo sistema → gateway → persona; (b) las relaciones hacia bases de datos usan `--->` (flecha más larga) para que sus etiquetas tengan una franja propia y no choquen con los servicios; (c) en C2/C2b la relación `shipmentSvc → broker` se declara antes que `ingestion → broker` para que ambas etiquetas no se crucen. Las relaciones de notificación hacia personas (`-.->`) van punteadas para distinguir el canal push/email/SMS de las interacciones HTTPS.

## Verificación contra la guía del mini-curso C4

Contrastado contra la síntesis de [MD/Guías y Material del Docente/Guía · Mini-curso_ El modelo C4 para documentar arquitectura de software.md](<../../MD/Guías y Material del Docente/Guía · Mini-curso_ El modelo C4 para documentar arquitectura de software.md>), usando el checklist de la sección 7 (errores comunes):

- [x] Toda relación tiene verbo + tecnología entre corchetes cuando aporta valor (corregido: las dos relaciones `notif → operador/cliente` en C1 no tenían tecnología; se añadió `[Push/Email/SMS]`).
- [x] Todo rol humano (operador, cliente, conductor) se dibuja como persona (`[Persona]`, con el color de persona de C4), nunca con notación de sistema.
- [x] El nivel de Contexto (C1) no expone ningún contenedor ni tecnología interna — Kafka, MQTT, PostgreSQL y TimescaleDB solo aparecen desde C2 en adelante.
- [x] Cada nivel responde solo su propia pregunta de zoom: C1 no descompone el sistema, C2 no entra a un solo contenedor (eso sería C3, aún no generado).
- [x] Notación limitada al set de elementos que enseña la guía (persona, sistema / sistema externo, contenedor, contenedor de base de datos, relación con verbo + tecnología) — cada caja declara su tipo entre corchetes. Kafka se modela como contenedor normal con la tecnología en el rótulo (no como forma de cola aparte), igual que el ejemplo propio del mini-curso (`Servicio de notificaciones [Contenedor: Cola + workers]`).

No se generó todavía el nivel de Componentes (C3) ni las vistas complementarias (Despliegue, Dinámico) — quedan pendientes si se necesitan para una entrega específica.

## Correcciones de diseño aplicadas

**1. Doble escritura en el Servicio de Ingesta.** Antes, Ingesta escribía la lectura cruda en la BD de series de tiempo **y** publicaba el evento en Kafka — dos escrituras independientes con riesgo de inconsistencia si el proceso fallaba entre una y otra. Se corrigió a **un solo escritor**: Ingesta únicamente publica `LecturaRecibida` en Kafka; el Servicio de Consultas (dueño del lado de lectura en CQRS) materializa esa lectura en la BD de series de tiempo al consumir el evento. Kafka queda como única fuente de verdad del flujo de lecturas — coherente con el NFR de "no perder lecturas aunque falle un componente".

**2. Acceso directo de Envíos a la base de datos de Alertas.** Antes, la verificación de "alertas críticas sin resolver" en la saga de cierre (HU-08) se modeló como `shipmentSvc` leyendo `alertDb` por JDBC — rompe el principio de que cada microservicio es dueño exclusivo de su base de datos, y acopla el esquema interno de Alertas a Envíos. Se corrigió a una **llamada síncrona de servicio a servicio** (`shipmentSvc → alertSvc` por REST): Envíos le pregunta a Alertas por su API, no por su base de datos. Queda como alternativa válida a futuro un enfoque coreografiado (Alertas publica eventos de resolución/generación, y Envíos mantiene una vista local de "alertas críticas abiertas por envío"), que evitaría la dependencia síncrona entre ambos servicios a costa de una consistencia con más retraso — no se aplicó todavía por ser más compleja de razonar para esta etapa del proyecto.

**3. Pendiente — lecturas SQL directas del Servicio de Consultas a `shipmentDb`/`alertDb`.** El mismo problema del punto 2 aparece en `queryApi → shipmentDb` y `queryApi → alertDb`: es lectura, no escritura, así que es más tolerable (patrón común de "reporting reads" en CQRS), pero sigue acoplando `queryApi` al esquema interno de otros servicios. La corrección consistente sería que Envíos y Alertas también publiquen eventos de cambio de estado (harían falta, p. ej., `AlertaResuelta`) y que `queryApi` materialice su propia copia de envíos y alertas igual que ya hace con las lecturas — eliminando toda lectura SQL cruzada entre servicios. No aplicada todavía, pendiente de decisión.

**4. Simplificación del C2 al alcance de un proyecto universitario.** El C2 original tenía 13 contenedores: app web, app móvil de conductor, API Gateway, broker MQTT y 9 más. Para un equipo de curso eso es más infraestructura de la que conviene sostener sin diluir el foco en los patrones que sí son objeto de evaluación (EDA, microservicios, CQRS, saga, DDD/hexagonal). Se simplificó a 9 contenedores sin renunciar a ningún patrón arquitectónico: **(a)** una sola **Aplicación Web** para operador, cliente y conductor — no hay justificación en las HU para dos clientes distintos; **(b)** sin **API Gateway** como contenedor propio — con un único cliente y solo dos servicios expuestos (Envíos, Consultas), el gateway no resuelve un problema real a esta escala, la app web les llama directo; **(c)** sin **broker MQTT** como contenedor propio — se modela como el listener MQTT embebido del Servicio de Ingesta, que sigue siendo el único punto de entrada de telemetría. Si el proyecto crece (más tipos de cliente, más servicios detrás del gateway), estas piezas se reintroducen sin tocar el resto del modelo.
