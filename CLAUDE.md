# Proyecto de Asignatura — Ingeniería de Software II (UPTC)

Repositorio de trabajo para el proyecto de grupo de la asignatura Ingeniería de Software II (código 8108267, UPTC). El curso avanza arquitectura de software sobre un proyecto propio por grupo — historias de usuario, DDD, arquitectura hexagonal, microservicios, CQRS, EDA, observabilidad/saga, persistencia políglota, CI/CD y arquitecturas híbridas.

## Proyecto asignado — Grupo G6

**Cadena de frío / logística con sensores IoT.** Monitoreo de temperatura y humedad de contenedores refrigerados durante transporte, con alertas automáticas de quiebre de cadena de frío. Ver [MD/Guías y Material del Docente/Requisitos_G6.md](MD/Gu%C3%ADas%20y%20Material%20del%20Docente/Requisitos_G6.md) para historias de usuario, reglas de negocio, requisitos no funcionales y entidades del dominio.

## Regla general: `MD/` es un clon en espejo de la raíz del proyecto

`MD/` vive en la raíz del repositorio y reproduce la misma estructura de carpetas que la raíz (`Docs/`, `Guías y Material del Docente/`, etc.). Siempre que se agregue, descargue o genere un documento no-Markdown (`.docx`, `.pdf`, `.pptx`, `.html`, etc.) en cualquier carpeta del proyecto, se debe generar también su versión `.md` dentro de `MD/`, en la ruta que replica exactamente dónde vive el original.

Ejemplo: un documento en `Guías y Material del Docente/Requisitos_G6.docx` tiene su `.md` en `MD/Guías y Material del Docente/Requisitos_G6.md` — misma ruta relativa, misma nombre base, raíz distinta. Esto aplica tanto si el documento lo trae el usuario como si el propio asistente lo crea. `code/` no se espeja (no contiene documentos, contiene código fuente).

## Estructura del repositorio

- **`Docs/`** — Documentación propia del proyecto de grupo (análisis, diseño, ADRs, diagramas, actas, etc.). Se llena a medida que avanza el curso.
- **`code/`** — Código fuente del proyecto (stack del curso: Spring Boot).
- **`Guías y Material del Docente/`** — Material entregado por el docente (guías, talleres, presentaciones, requisitos de cada grupo), en su formato original tal como se descarga de Moodle.
- **`MD/`** — Espejo en Markdown de todo documento no-Markdown del proyecto (ver regla arriba). No es material de estudio para los integrantes del grupo — para eso están los originales; el `MD/` es un espejo de trabajo para la IA.
  - **`<misma-ruta-que-el-original>/assets/<nombre-doc>/`** — Imágenes extraídas del documento original, referenciadas desde el `.md` correspondiente.

## Convención al agregar material nuevo del docente

1. Descargar el archivo original de Moodle → guardarlo en `Guías y Material del Docente/`, tal cual.
2. Generar su equivalente en Markdown en `MD/Guías y Material del Docente/` (mismo nombre base, extensión `.md`).
   - Para `.docx`: no hay `pandoc` instalado en este entorno; usar Python estándar (`zipfile` + `xml.etree`) para extraer `word/document.xml` y convertir párrafos, tablas y estilos de heading a Markdown. Extraer las imágenes referenciadas (`word/media/`) a `MD/Guías y Material del Docente/assets/<nombre-doc>/` y enlazarlas en el `.md`.
   - Para `.html`: convertir a Markdown conservando la estructura de encabezados y listas.
3. Mantener el mismo nombre base y la misma ruta relativa (bajo `MD/`) que el original, para ubicar fácilmente uno a partir del otro.

## Estado actual del material

- Listo: Requisitos del proyecto G6 (`Requisitos_G6.docx` / `Requisitos_G6.md`).
- Pendiente: guías, talleres y presentaciones de las lecciones del curso — requieren descarga manual desde Moodle (sesión autenticada; no accesibles vía fetch automático porque quedan detrás del login).
