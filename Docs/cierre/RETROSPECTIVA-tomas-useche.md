# Retrospectiva individual — Tomás Useche

## Autoevaluación (1 = no lo logro, 4 = lo hago con autonomía)

RA1 Explico principios, patrones y estilos arquitectónicos .......... 3
RA2 Analizo ventajas, limitaciones y contextos de los estilos ........ 3
RA3 Diseño arquitecturas con modelos, diagramas y estándares ........ 3
RA5 Implemento arquitecturas modernas (microservicios, EDA, CQRS,
    DDD, hexagonal) ................................................ 4

Evidencias:

- **RA1:** las notas de cada taller en `rica-api/NOTAS.md` (repositorio `usechet1/rica-api`), por
  ejemplo la explicación de por qué la DLT es mejor que reintentar sin fin (rama `taller-eda`,
  commit `15fe8d4`) y la del bloqueo semántico en la Saga (rama `taller-observabilidad`, `b0bd3c6`).
- **RA2:** la matriz de decisión de este proyecto (`Docs/cierre/MATRIZ.md`), donde el análisis de
  sensibilidad muestra que nuestra elección le gana al monolito modular por un solo punto si el
  costo de operación pesa más; y los ADR con alternativas descartadas en `Docs/ARQUITECTURA.md`.
- **RA3:** el diagrama de contenedores y las tablas de conexiones y eventos de
  `Docs/ARQUITECTURA.md`, y el ATAM-lite (`Docs/cierre/ATAM-LITE.md`).
- **RA5:** en los talleres, una rama por estilo: `taller-hexagonal` (`f8f8f84`),
  `taller-microservicios` con gRPC, Circuit Breaker, Eureka, Gateway, Outbox y Saga (`fbdd6a2`),
  `taller-cqrs` (`e1c4f71`), `taller-eda` (`15fe8d4`), `taller-observabilidad` (`b0bd3c6`) y
  `taller-contenedores` con Docker y CI (`93cdd8c`). En el proyecto, la extracción de
  `alertas-service` (`398417a`), el cliente HTTP con Circuit Breaker (`ac0edbb`) y el consumidor
  Kafka idempotente con DLT.

## Tres aprendizajes que me llevo

1. Un puerto bien diseñado paga de verdad: cuando Alertas se fue a otro servicio, en Envíos solo
   cambió el adaptador (`ConsultaAlertasCriticasHttp`); `CierreEnvioService` ni se enteró. Es lo
   mismo que vi en RICA cuando cambié `VerificadorInvestigadorPendiente` por el adaptador gRPC.
2. Kafka entrega "al menos una vez", y un duplicado no avisa: en el taller de EDA el contador pasó
   de 6 a 12 sin un solo error en el log. Por eso en el proyecto hice que el registro del evento y
   la alerta se guarden en la misma transacción.
3. Lo que funciona "en mi máquina" no es lo que se evalúa: el `.gitignore` de lista blanca del
   proyecto hizo que mi primer commit de `alertas-service` saliera vacío, y en RICA tuve tres
   identidades de Git distintas sin saberlo. Revisar `git status` antes de cada commit se volvió
   costumbre.

## Una decisión de arquitectura que tomaría distinto hoy, y por qué

Extraería los servicios desde el principio por contexto delimitado, en vez de construir todo en un
solo proyecto y separarlo al final. En el proyecto, Alertas vivió meses dentro del monolito y
Santiago tuvo que crear un repositorio en memoria provisional para poder avanzar; al extraerlo hubo
que reconciliar dos versiones del mismo puerto. Si hubiéramos acordado los contratos (la API de
alertas y el evento de sensores) en la Lección 5, cada uno habría trabajado sobre su servicio sin
pisar al otro.

## Lo que me falta por aprender y cómo lo haría

- **Observabilidad de verdad:** en el taller 10 propagué un id de correlación a mano; me falta usar
  trazas distribuidas (OpenTelemetry) y un tablero real. Lo haría instrumentando `alertas-service`
  con OpenTelemetry y viendo las trazas en Jaeger.
- **Kafka en producción:** solo he usado un broker. Quiero entender réplicas, particiones y el
  orden de los eventos entre tópicos (la "resurrección" que vi en el taller de CQRS). Lo practicaría
  levantando un clúster de 3 brokers con Docker Compose.
- **Pruebas de contrato** entre servicios, para que un cambio en el evento de sensores rompa una
  prueba y no la producción.

## Mi aporte al proyecto de grupo (con evidencia de commits)

- **Subdominio de Alertas, DDD (septiembre):** value object `SeveridadAlerta` (`7da2c2b`), servicio de
  dominio `EscalamientoAlertaService` (`0e8402e`), `AlertaFactory` (`ee55acb`), límite del agregado
  (`884bf94`) y lenguaje ubicuo (`9747115`).
- **Arquitectura hexagonal de Alertas:** `c1480dc` e integración sobre `main` (`010ee1b`).
- **Extracción de `alertas-service`:** servicio independiente con su base, API REST, Actuator y
  Dockerfile (`398417a`); el monolito consulta Alertas por HTTP con Circuit Breaker (`ac0edbb`);
  consumidor Kafka idempotente con DLT.
- **Documentos de arquitectura y cierre:** `Docs/ARQUITECTURA.md` y `Docs/cierre/` (Lecciones 12 y 13).

Comando para verificarlo: `git log --oneline --author="Tomas Useche"` (con el `.mailmap`, incluye
también los commits hechos como `usechet`).
