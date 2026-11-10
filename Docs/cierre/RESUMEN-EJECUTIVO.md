# Resumen ejecutivo - Sistema de Cadena de Frío

Pedimos: aprobar que el sistema siga separado en dos partes independientes, una que gestiona los
envíos y otra que vigila las alertas, y dar al equipo un mes más para conectar los sensores reales.

Por qué: cuando un medicamento o un alimento viaja fuera de su temperatura, alguien tiene que
enterarse a tiempo y el envío no puede entregarse como si nada hubiera pasado. Hoy esa vigilancia
depende de que todo el sistema esté funcionando a la vez: si falla una parte, se dejan de vigilar
los contenedores.

Qué proponemos: dividir el sistema en dos partes que funcionan por separado. Una registra y cierra
los envíos; la otra recibe las mediciones de los sensores y genera las alertas. Si la parte de
alertas se detiene un rato, las mediciones esperan en una fila y se revisan apenas vuelve, sin
perder ninguna. Y ningún envío se puede cerrar mientras tenga una alerta grave sin atender.

Qué ganamos / qué cuesta:
- Ganamos: ninguna medición fuera de rango se pierde, y las reglas de las alertas se pueden cambiar
  sin detener el registro de envíos.
- Cuesta: hay dos programas y dos bases de datos que mantener en lugar de uno, y más tiempo del
  equipo para ponerlos en marcha.

Principales riesgos y cómo los controlamos:
1. Los sensores todavía no envían sus mediciones al sistema nuevo: es lo primero que haremos en
   el mes que pedimos.
2. Si la parte de alertas se detiene, no se puede cerrar ningún envío. Lo aceptamos a propósito:
   preferimos retrasar un cierre a cerrar un envío dañado sin saberlo.

Siguiente paso: el equipo conecta los sensores reales y mide cuánto tarda en aparecer una alerta;
lo presentamos en la socialización del proyecto.
