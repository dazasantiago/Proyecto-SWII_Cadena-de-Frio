---
fuente: Guías y Material del Docente/Guía · Mini-curso_ El modelo C4 para documentar arquitectura de software.html
fuente-original-url: https://presencial.uptc.edu.co/pluginfile.php/60503/mod_resource/content/2/mini-curso-c4/guia.html
tipo: Mini-curso independiente (recurso del docente)
---

# El modelo C4 para documentar arquitectura de software

> Síntesis de guía de la IA — no es material de estudio para el grupo (para eso está el original en `Guías y Material del Docente/`). Extrae solo el contenido sustantivo del HTML (se omite maquetación, navegación y estilos).

## Objetivos de aprendizaje del mini-curso

- Explicar el problema que resuelve C4 frente a los diagramas ad-hoc y su idea central de abstracción por niveles.
- Aplicar la notación exacta de los niveles de Contexto, Contenedores y Componentes sobre un caso propio.
- Reconocer cuándo usar el nivel de Código, el diagrama de Despliegue y el diagrama Dinámico.
- Detectar y corregir los errores más frecuentes al modelar con C4.
- Comparar herramientas para producir diagramas C4 como código.

## 1. Por qué existe el modelo C4

Los diagramas de arquitectura ad-hoc (cajas y flechas sin convención compartida) tienen tres problemas recurrentes:

1. **Ambigüedad de las cajas** — una caja puede ser un servidor físico, un microservicio, una clase de código o un departamento, sin que nada lo distinga.
2. **Mezcla de niveles de abstracción** — poner al mismo nivel visual "todo el sistema de pagos de un tercero" y "una función interna de validación".
3. **Relaciones mudas** — flechas sin etiqueta que no dicen qué acción ocurre ni con qué tecnología.

**La analogía del mapa** (Simon Brown, creador del modelo): igual que en Google Maps se hace zoom de país → ciudad → calle sin perder el sentido de ubicación, C4 define cuatro "niveles de zoom" consistentes — **C**ontexto, **C**ontenedores, **C**omponentes y **C**ódigo — cada uno respondiendo una pregunta distinta con notación mínima y compartida.

**Prueba de fuego:** un diagrama C4 bien hecho se entiende sin que su autor esté presente para explicarlo. Si alguien nuevo en el equipo pregunta "¿y esta caja qué es?", el diagrama no cumple su función.

## 2. Nivel 1 — Diagrama de Contexto

Punto de partida obligatorio: el sistema completo como una única caja, rodeada de las personas que lo usan y los sistemas externos con los que intercambia información. Debe poder leerse en menos de un minuto, técnico o no.

| Elemento | Forma / color | Significado |
|---|---|---|
| Persona | Caja oscura, ícono de persona | Un rol humano: quién usa el sistema |
| Sistema de software (el nuestro) | Caja dorada, en el centro | El sistema que se está documentando |
| Sistema de software externo | Caja gris | Otro sistema con el que se integra, fuera de nuestro control |
| Relación | Flecha con etiqueta corta | Verbo de acción + protocolo opcional entre corchetes |

**Regla de oro de las relaciones:** toda flecha se etiqueta con un verbo ("Consulta", "Envía notificación a") y, si aporta valor, la tecnología entre corchetes ("[HTTPS]"). Una flecha sin etiqueta es de las razones más comunes por las que un diagrama C4 falla en su propósito.

Ejemplo del curso (dominio "Red de Investigación y Colaboración Académica"): [red_context.png](assets/mini-curso-c4/red_context.png)

## 3. Nivel 2 — Diagrama de Contenedores

El nivel más usado en la práctica profesional. Hace zoom hacia adentro de la caja única del nivel anterior, mostrando las piezas que se pueden ejecutar y desplegar de forma independiente: una app web, una API, una app móvil, una base de datos, un worker de colas.

> **Cuidado con el nombre** — "contenedor" en C4 **no** significa Docker ni ninguna tecnología de contenerización. Es un término genérico de Simon Brown anterior a la popularización de Docker: simplemente "algo que se ejecuta por separado".

| Elemento | Forma / color | Significado |
|---|---|---|
| Contenedor | Caja dorada, esquinas redondeadas | Pieza desplegable, con su tecnología entre corchetes |
| Base de datos | Cilindro dorado | Un contenedor especializado en almacenamiento |
| Personas y sistemas externos | Igual que en Contexto | Se conservan tal cual, sin descomponer |
| Relación | Flecha etiquetada con tecnología | "Lee/escribe [JDBC]", "Publica evento [AMQP]" |

Ejemplo del curso: [red_container.png](assets/mini-curso-c4/red_container.png)

## 4. Nivel 3 — Diagrama de Componentes

Hace zoom hacia adentro de **un solo** contenedor, mostrando cómo se organiza su código en agrupamientos con responsabilidad clara: un controlador, un servicio de dominio, un repositorio. A diferencia de Contexto y Contenedores, **este nivel es opcional** — no todos los contenedores lo necesitan.

| Cuándo sí vale la pena | Cuándo no vale la pena |
|---|---|
| El contenedor es complejo, lo va a tocar mucha gente, o hay que justificar una decisión de diseño interna | El contenedor es simple, o el detalle envejece tan rápido que nadie lo mantiene actualizado |

| Elemento | Forma / color | Significado |
|---|---|---|
| Componente | Caja azul claro, dentro del contenedor | Agrupamiento de código: controlador, servicio, repositorio |
| Frontera del contenedor | Recuadro punteado | Todo lo de adentro vive en la misma pieza desplegable |

Ejemplo del curso (contenedor "API backend"): [component_red_investigacion.png](assets/mini-curso-c4/component_red_investigacion.png) — tres controladores delegan en servicios de dominio, que a su vez usan repositorios o clientes externos.

## 5. Nivel 4 y vistas complementarias

**Nivel de Código:** clases, interfaces y sus relaciones (mismo detalle que un diagrama UML de clases). Simon Brown recomienda generarlo automáticamente desde el código (IDE / ingeniería inversa), nunca a mano — el código cambia constantemente y un diagrama manual queda desactualizado en días. En la industria casi nadie lo dibuja a mano; el mini-curso no lo practica.

Dos vistas complementarias (no son "niveles de zoom", responden otras preguntas):

| Vista | Pregunta que responde | Cuándo usarla |
|---|---|---|
| Diagrama de Despliegue | ¿En qué infraestructura corre cada contenedor? | Mapea contenedores sobre nodos de infraestructura (servidores, instancias cloud, clústeres). Puede haber una por ambiente (dev/pruebas/producción) |
| Diagrama Dinámico | ¿En qué orden ocurren las llamadas de un caso de uso? | Mismas cajas de los diagramas estáticos, pero numeradas en orden — similar a un diagrama de secuencia. Útil para flujos no obvios: autenticación con redirecciones, procesos asíncronos con colas |

## 6. Segundo ejemplo completo: Reservas de Salas de Estudio

**Escenario:** una universidad quiere que sus estudiantes reserven salas de estudio desde el celular, evitando choques de horario, y que la biblioteca configure qué salas y horarios están disponibles. El sistema debe autenticar contra el proveedor institucional de identidad ya existente.

- Contexto: [salas_context.png](assets/mini-curso-c4/salas_context.png)
- Contenedores: [salas_container.png](assets/mini-curso-c4/salas_container.png)

> **Nota de diseño del ejemplo** — el servicio de notificaciones se separó como contenedor propio porque procesa de forma asíncrona (cola + workers): si fallara, no debe bloquear el flujo principal de reserva. Esta clase de decisión — qué se separa y por qué — es exactamente lo que un diagrama de Contenedores debe dejar visible.

## 7. Errores comunes al aplicar C4

Ilustrados antes/después sobre un tercer dominio (sistema de biblioteca):

- Con errores: [biblioteca_bad.png](assets/mini-curso-c4/biblioteca_bad.png)
- Corregido: [biblioteca_good.png](assets/mini-curso-c4/biblioteca_good.png)

1. **Relación sin etiqueta** — una flecha ("Lector" → sistema) que no dice qué hace ni con qué protocolo obliga a adivinar.
2. **Una persona modelada como sistema** — un rol humano (p. ej. "Administrador") dibujado con notación de sistema de software (caja dorada/gris) en vez de caja oscura de persona. Mezclar notaciones rompe la lectura.
3. **Un contenedor filtrado al nivel de Contexto** — un detalle de implementación interno (p. ej. "PostgreSQL") no debe aparecer en Contexto; el sistema ahí es una sola caja sin abrir su interior. Ese detalle pertenece al diagrama de Contenedores.

**Checklist derivado para autorrevisión de cualquier diagrama C4:**
- [ ] ¿Toda flecha tiene verbo + tecnología entre corchetes cuando aporta valor?
- [ ] ¿Todo rol humano usa notación de Persona, nunca de Sistema?
- [ ] ¿El nivel de Contexto no muestra ningún contenedor/tecnología interna (BD, framework, etc.)?
- [ ] ¿Cada nivel responde solo su propia pregunta de zoom, sin mezclar escalas?

## 8. Herramientas para producir diagramas C4

Práctica recomendada en el trabajo profesional: tratar los diagramas como código (versionados junto al repositorio, regenerados automáticamente).

| Herramienta | Enfoque | Cuándo conviene |
|---|---|---|
| Structurizr | DSL propio de Simon Brown; un solo modelo genera los 4 niveles | Documentar un sistema real que se mantiene en el tiempo, con una única fuente de verdad |
| PlantUML C4 | Extensión de PlantUML con macros C4 (Person, System, Container...) | Equipos que ya usan PlantUML y quieren diagramas como código junto al repositorio |
| Mermaid C4 | Sintaxis C4 nativa en Mermaid, soportada en Markdown/GitHub | Documentación rápida integrada en un README o wiki, sin herramientas externas |

## Referencias (del original)

1. L. Bass, P. Clements, and R. Kazman, *Software Architecture in Practice*, 4th ed. Addison-Wesley, 2021.
2. M. Nygard, "Documenting Architecture Decisions," Cognitect Blog, Nov. 2011.
3. S. Brown, *The C4 Model for Visualising Software Architecture*. https://c4model.com
4. S. Brown, "Structurizr DSL," Structurizr Documentation. https://structurizr.com/dsl
5. M. Richards and N. Ford, *Fundamentals of Software Architecture: An Engineering Approach*. O'Reilly Media, 2020.
6. Mermaid, "C4 Diagrams," Mermaid Documentation. https://mermaid.js.org/syntax/c4.html
