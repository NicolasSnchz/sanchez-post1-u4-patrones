# Post-contenido — Unidad 4: Patrones de Comportamiento en ComprasUDES

## Descripción

Repositorio del post-contenido de la Unidad 4 de Patrones de Diseño de Software. Es un único proyecto Spring Boot (`compras-comportamiento`) que resuelve cuatro necesidades reales del backend de ComprasUDES, el sistema interno de solicitudes de compra corporativas: aprobación por niveles jerárquicos, ejecución reversible de solicitudes aprobadas, notificaciones ante cambios de estado y reglas de transición según el estado actual de la solicitud.

Autor: Nicolás Sánchez
Curso: Patrones de Diseño de Software, UDES

## Cómo ejecutar

```
mvn clean package
mvn spring-boot:run
mvn test
```

La aplicación levanta en el puerto 8080 y expone el endpoint `POST /api/solicitudes/evaluar`, que recibe una solicitud en JSON y devuelve el resultado de la evaluación.

## Estructura del proyecto

```
src/main/java/com/universidad/compras/
├── ComprasApp.java
├── modelo/
│   └── Solicitud.java                      (dado, no modificado)
├── aprobacion/                             Necesidad 1
│   ├── ServicioAprobacion.java             (dado)
│   ├── ResultadoAprobacion.java            (dado)
│   ├── ControladorSolicitudes.java         (dado)
│   ├── NivelAprobacion.java
│   ├── SupervisorArea.java
│   ├── GerenteArea.java
│   ├── DirectorFinanciero.java
│   ├── RevisorCumplimientoNormativo.java
│   └── CadenaAprobacion.java
├── ejecucion/                              Necesidad 2
│   ├── PresupuestoService.java             (dado)
│   ├── OrdenCompraService.java             (dado)
│   ├── ComandoEjecucion.java
│   ├── ReservarPresupuestoComando.java
│   ├── GenerarOrdenCompraComando.java
│   └── InvocadorEjecucion.java
├── notificacion/                           Necesidad 3
│   ├── ClientesNotificacion.java           (dado)
│   ├── EventoCambioEstado.java
│   ├── SuscriptorEstado.java
│   ├── PublicadorCambioEstado.java
│   ├── NotificadorCorreo.java
│   ├── ActualizadorContabilidad.java
│   └── RegistroAuditoria.java
└── estado/                                 Necesidad 4
    ├── EstadoSolicitud.java
    ├── EstadoBase.java
    ├── EstadoPendiente.java
    ├── EstadoEnAprobacion.java
    ├── EstadoAprobada.java
    ├── EstadoEjecutada.java
    ├── EstadoRechazada.java
    ├── EstadoCancelada.java
    ├── FabricaEstados.java
    └── ContextoSolicitud.java
```

## Decisiones de diseño

### Necesidad 1 — Aprobación por niveles jerárquicos

Patrón aplicado: **Chain of Responsibility**.

El síntoma que describe el enunciado es que una solicitud tiene que recorrer varios decisores en un orden dado hasta que uno de ellos se declare competente y la resuelva, y que ese recorrido debe poder crecer, encogerse o reordenarse sin que quien dispara la evaluación se entere. `ControladorSolicitudes` solo conoce la interfaz `ServicioAprobacion` y llama a `evaluar`, sin saber cuántos niveles hay ni en qué orden se consultan.

`NivelAprobacion` es el eslabón: cada nivel decide con `esDeMiCompetencia` si la solicitud le corresponde, y si no le corresponde la pasa al siguiente sin saber quién viene después de ese. `SupervisorArea` y `GerenteArea` definen su competencia por monto, `DirectorFinanciero` cierra la cadena sin límite superior y `RevisorCumplimientoNormativo` la define por categoría. `CadenaAprobacion` es el único lugar donde se dice qué niveles existen y en qué orden, en el método `nivelesPara`. Agregar el nivel de cumplimiento normativo consistió exactamente en eso: una clase nueva y una línea en esa lista, sin tocar el controlador ni ninguno de los otros tres niveles.

Alternativa descartada: **Command**. Command encapsula una operación concreta en un objeto para poder ejecutarla, guardarla y revertirla. Aquí no hay ninguna operación que revertir ni ninguna acción que encolar: hay una petición entrante que debe encontrar a su decisor. Si se hubiera modelado cada nivel como un comando, alguien tendría que ir preguntando comando por comando cuál aplica, y ese alguien terminaría siendo un `if` o un `for` en el código cliente que sabe cuántos niveles hay, que es justamente la restricción que el enunciado prohíbe. En Chain of Responsibility esa decisión vive dentro de cada eslabón y el cliente no participa.

Nota sobre el nivel de cumplimiento normativo: el enunciado pide que se evalúe antes del nivel que corresponda por monto, y la prueba `solicitudInternacionalPasaPorCumplimientoAntesDelNivelPorMonto` espera que quede registrado como resolutor de la solicitud S-003. Por eso su competencia se definió por categoría y no por monto: es el primer eslabón de la cadena, resuelve las solicitudes INTERNACIONAL y delega todo lo demás al nivel que corresponda.

### Necesidad 2 — Ejecución reversible de solicitudes aprobadas

Patrón aplicado: **Command**.

Aquí el síntoma es distinto: no hay decisores ni delegación. Hay dos operaciones concretas, reservar presupuesto y generar la orden de compra, que un mismo actor decide ejecutar, que deben poder deshacerse por separado y que deben quedar registradas en orden para consultarse después. Eso es exactamente lo que hace Command: cada operación se vuelve un objeto que sabe hacerse y deshacerse solo.

`ReservarPresupuestoComando` y `GenerarOrdenCompraComando` envuelven a `PresupuestoService` y `OrdenCompraService` sin modificarlos ni reimplementar su lógica, y guardan por dentro lo que necesitan para revertir: el flag de reserva en el primero, el número de orden y el estado previo de la solicitud en el segundo. `InvocadorEjecucion` dispara los comandos sin saber qué hace ninguno por dentro y mantiene dos registros por solicitud: el historial completo, que nunca se borra, y la pila de operaciones todavía aplicadas, que es de donde se deshace. Esa separación es la que permite que `deshacerUltima` revierta solo la generación de la orden dejando intacta la reserva, que `deshacer(id, indice)` revierta una operación puntual, y que el historial siga reportando las dos operaciones aun después de deshacer una.

Alternativa descartada: **Chain of Responsibility**, el patrón de la Necesidad 1. No aplica porque no hay una petición buscando quién la resuelva. Las dos operaciones no compiten entre sí ni son alternativas: se ejecutan las dos, en orden, y siempre sobre la misma solicitud. Una cadena tendría que responder qué eslabón se queda con la petición, y aquí la respuesta es que ninguno, porque el problema no es a quién le toca sino cómo dejar registrada y reversible una acción que ya se decidió hacer. Además, una cadena no tiene ningún mecanismo para deshacer nada: el eslabón que resuelve termina y no queda objeto alguno que guarde cómo revertir lo hecho.

### Necesidad 3 — Notificaciones ante cambio de estado

Patrón aplicado: **Observer**.

El problema es que cuando una solicitud cambia de estado, tres módulos ajenos a ella tienen que enterarse y actuar, y ni la solicitud ni el código que la modifica deben conocerlos. Ese desacople entre quien produce el hecho y quienes reaccionan a él es lo que resuelve Observer.

`PublicadorCambioEstado` es el sujeto observado y el único punto por donde pasa un cambio de estado en todo el sistema: actualiza la solicitud y avisa a todos los suscriptores registrados. `SuscriptorEstado` es el contrato que cumplen `NotificadorCorreo`, `ActualizadorContabilidad` y `RegistroAuditoria`, cada uno usando el método que le corresponde de `ClientesNotificacion` sin reimplementar nada. El publicador no menciona por su nombre a ninguno de los tres: recibe la lista por constructor y expone `suscribir` para agregar más. La prueba `agregarUnCuartoSuscriptorDePruebaNoRequiereModificarElMecanismo` registra un colector escrito en la propia clase de prueba y verifica que reacciona, sin haber tocado una línea del publicador.

El mecanismo quedó conectado a los puntos de cambio de estado que ya existían: `CadenaAprobacion` lo usa para pasar la solicitud a EN_APROBACION y luego a APROBADA o RECHAZADA, `GenerarOrdenCompraComando` para dejarla en EJECUTADA y para devolverla a su estado previo cuando se deshace, y `ContextoSolicitud` para cada transición de la Necesidad 4.

Alternativa descartada: **State**, el patrón de la Necesidad 4. La diferencia está en de quién es el comportamiento que cambia. En State, lo que cambia es el comportamiento de la propia solicitud: qué se le puede hacer según en qué punto de su historia esté. Aquí la solicitud ya cambió y su comportamiento no está en discusión; lo que hace falta es que el correo, la contabilidad y la auditoría, que no son parte de la solicitud ni saben nada de ella, se enteren. Modelar esto con State significaría meter dentro de cada objeto de estado el envío de correos y la actualización del tablero, con lo cual agregar una cuarta reacción obligaría a modificar los seis estados, que es lo contrario de lo que pide el enunciado.

### Necesidad 4 — Reglas de transición según el estado actual

Patrón aplicado: **State**.

El síntoma es un `if/else` sobre `getEstado()` repetido con variaciones en varios métodos, que crece cada vez que aparece un estado nuevo. La solución no era esconder ese `if` detrás de un método sino eliminar la pregunta: cada estado pasa a ser un objeto que sabe qué operaciones admite mientras la solicitud está en él.

`ContextoSolicitud` no decide nada, delega `aprobar`, `rechazar`, `ejecutar` y `cancelar` en el objeto de estado actual. `EstadoBase` deja todas las operaciones rechazadas por defecto y cada estado concreto declara únicamente lo que sí permite: `EstadoAprobada` es el único que implementa `ejecutar`, `EstadoEjecutada` no implementa ninguna, y así. Una operación no permitida devuelve un mensaje de error y no toca el estado, que es lo que verifican las pruebas de S-031 y S-032. Cuando la operación sí es válida, el propio objeto de estado llama a `transicionarA` y el contexto pasa a otro estado. Agregar EN_ESPERA_PROVEEDOR el próximo semestre es escribir una clase y agregar una línea en `FabricaEstados`, sin revisar ningún método existente para ver si quedó una combinación sin cubrir.

Alternativa descartada: **Strategy**, que estructuralmente se ve casi igual, con una interfaz y varias implementaciones intercambiables detrás de un contexto. La diferencia es quién elige y cuándo. En Strategy el cliente elige la implementación desde afuera y se la inyecta al contexto, como el carrito que activa la estrategia de descuento que quiere usar en ese momento; las estrategias son intercambiables entre sí en cualquier llamada y ninguna sabe de la existencia de las otras. Aquí ningún código externo elige: la solicitud tiene un solo comportamiento válido en cada instante y es el que le impone el estado en que se encuentra. Además, los objetos de estado se reemplazan entre ellos como parte de resolver la operación, cosa que una estrategia nunca hace: una estrategia de descuento no decide que a partir de ahora se debe usar otra estrategia. Esa transición interna es precisamente lo que aquí hace falta y lo que Strategy no modela.

### Reflexión — otros tres patrones

**Iterator** encajaría en el reporte que recorre secuencialmente las solicitudes de un centro de costo, porque permite entregar un recorrido uniforme sin que el reporte sepa si por dentro hay una lista, un mapa o algo distinto, y sin obligar a cambiar el reporte si mañana cambia la estructura.

**Template Method** encajaría en los tres tipos de comprobante, porque el esqueleto de impresión con encabezado, cuerpo y pie se escribe una sola vez en una clase base que fija el orden de los pasos, y cada comprobante solo redefine el método que llena el cuerpo.

**Memento** sería lo más cercano a guardar y restaurar instantáneas completas de una solicitud. Se diferencia del undo de la Necesidad 2 en qué se guarda: Command guarda la operación y sabe cómo revertir ese cambio puntual, mientras que Memento guarda una copia del estado completo de la solicitud en un momento dado y permite volver a él de un salto, sin importar cuántas operaciones pasaron en el medio y sin que el módulo que custodia la instantánea vea nada de lo que hay dentro de `Solicitud`.

## Herramientas utilizadas

- Java 17, Spring Boot 3.2, Apache Maven, JUnit 5
- Visual Studio Code, Git, GitHub

## Conclusiones

Lo más difícil de este laboratorio no fue implementar los patrones sino separar problemas que a primera vista se parecen. Chain of Responsibility y Command aparecen juntos en la Parte 1 y ambos tienen que ver con procesar una solicitud, pero uno responde a quién le toca resolverla y el otro a cómo dejar reversible algo que ya se decidió hacer, y esa pregunta se contesta leyendo el enunciado, no mirando la forma del código. El caso más sutil fue Strategy contra State en la Necesidad 4, porque el diagrama de clases es prácticamente el mismo y la diferencia está en algo que no se ve ahí: quién elige el comportamiento y si ese comportamiento puede reemplazarse a sí mismo. También quedó claro que las restricciones del enunciado son la mejor pista disponible; cuando el requisito dice que agregar una reacción nueva no debe obligar a modificar el código que dispara el cambio, prácticamente está describiendo Observer. Al final, elegir bien el patrón se pareció más a leer con cuidado que a memorizar catálogos.
