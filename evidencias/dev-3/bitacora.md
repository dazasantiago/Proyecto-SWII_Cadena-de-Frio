# Bitácora de tiempo — Dev 3 · Alertas e incidentes

Registrar el tiempo **real** de cada paso y la fecha. Debe coincidir con el historial de Git (`git log --oneline --author="usechet"`).

| Paso | Tiempo estimado | Tiempo real | Fecha | Commit |
|---|---|---|---|---|
| 1 · Lenguaje Ubicuo (`glosario.md`) | ~20 min | ~35 min | 2026-09-24 | `9747115` |
| 2 · Value Object (`SeveridadAlerta`) | ~30 min | ~50 min | 2026-09-24 | `7da2c2b` |
| 3 · Servicio de Dominio (`EscalamientoAlertaService`) | ~35 min | ~1 h | 2026-09-24 | `0e8402e` |
| 4 · Límite del Agregado (`limite-agregado.md`) | ~15 min | ~25 min | 2026-09-24 | `884bf94` |
| 5 · Factory (`AlertaFactory`) | ~30 min | ~50 min | 2026-09-24 | `ee55acb` |
| 6 · Commit y Pull Request | ~10 min | ~20 min | 2026-09-24 | PR #2 |
| **Total** | **~2 h 20 min** | **~4 h** | | |

## Observaciones

- Este subdominio se construyó con asistencia de Claude Code, a partir del Event Storming ya cerrado en `Docs/NOTAS-equipo.md` y siguiendo el mismo patrón (record + validación en constructor compacto, excepciones dedicadas, Factory) que Dev 1 dejó en la rama `subdominio-envíos`.
- **Tiempo real**: ~4 h en total (estimación de buena fe del integrante responsable, Tomás Useche, repartida proporcionalmente a los tiempos estimados de cada paso — no viene de las marcas de tiempo de los commits, que reflejan la velocidad de la sesión asistida por IA y no el tiempo real de estudio/validación).
- Los 15 tests del proyecto (14 del subdominio `alertas` + 1 de arranque de la app) pasan en verde con Eclipse Temurin JDK 25 (instalado en esta máquina el 2026-09-24), sin overrides — coincide con el `java.version=25` declarado en el `pom.xml`. Salida en `evidencias/dev-3/capturas/salida-tests.txt`.
- Umbral de escalamiento (`EscalamientoAlertaService.LECTURAS_CONSECUTIVAS_PARA_ESCALAR = 3`) es un valor por defecto, no viene de un requisito explícito — ver `glosario.md` §5. Pendiente de confirmar con el equipo.
- Pendiente de confirmar con el equipo si `Incidente` pertenece a este subdominio o al de Envíos (ver `glosario.md` §5).
- Pendiente: capturas de pantalla reales del código y de los tests en verde en el IDE (`evidencias/dev-3/capturas/`). No pude generarlas desde este entorno de sesión (sin GUI); ver `evidencias/dev-3/capturas/README.md`.
