# Bitácora de tiempo — Dev 3 · Alertas e incidentes

Registrar el tiempo **real** de cada paso y la fecha. Debe coincidir con el historial de Git (`git log --oneline --author="usechet"`).

| Paso | Tiempo estimado | Tiempo real | Fecha | Commit |
|---|---|---|---|---|
| 1 · Lenguaje Ubicuo (`glosario.md`) | ~20 min | | 2026-09-24 | `a17b397` |
| 2 · Value Object (`SeveridadAlerta`) | ~30 min | | 2026-09-24 | `4a5f05e` |
| 3 · Servicio de Dominio (`EscalamientoAlertaService`) | ~35 min | | 2026-09-24 | `7f0e6fe` |
| 4 · Límite del Agregado (`limite-agregado.md`) | ~15 min | | 2026-09-24 | `3628bfe` |
| 5 · Factory (`AlertaFactory`) | ~30 min | | 2026-09-24 | `4ae9256` |
| 6 · Commit y Pull Request | ~10 min | | 2026-09-24 | |
| **Total** | **~2 h 20 min** | | | |

## Observaciones

- Este subdominio se construyó con asistencia de Claude Code, a partir del Event Storming ya cerrado en `Docs/NOTAS-equipo.md` y siguiendo el mismo patrón (record + validación en constructor compacto, excepciones dedicadas, Factory) que Dev 1 dejó en la rama `subdominio-envíos`.
- La columna **Tiempo real** queda pendiente de completar por el integrante responsable (Tomás Useche), ya que el tiempo de sesión de IA no equivale al tiempo real de estudio/validación del contenido por el estudiante.
- Los 15 tests del proyecto (14 del subdominio `alertas` + 1 de arranque de la app) pasan en verde, verificado localmente con `mvnw test` usando JDK 22 y `-Dmaven.compiler.release=22` como *override* de una sola ejecución (el `pom.xml` sigue declarando `java.version=25`, sin cambios, porque no hay JDK 25 instalado en esta máquina). Antes de dar el paso 6 por cerrado, correr `mvnw test` con JDK 25 real para confirmar que también compila con el release declarado en el `pom.xml`.
- Umbral de escalamiento (`EscalamientoAlertaService.LECTURAS_CONSECUTIVAS_PARA_ESCALAR = 3`) es un valor por defecto, no viene de un requisito explícito — ver `glosario.md` §5.
- Pendiente: capturas de pantalla reales del código y de los tests en verde en el IDE (`evidencias/dev-3/capturas/`). No pude generarlas desde este entorno de sesión (sin GUI); ver `evidencias/dev-3/capturas/README.md`.
