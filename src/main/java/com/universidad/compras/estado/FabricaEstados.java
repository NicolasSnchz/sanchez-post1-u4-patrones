package com.universidad.compras.estado;

import java.util.Map;
import java.util.function.Supplier;

// Traduce el estado que trae la Solicitud (un String) al objeto de estado
// que le corresponde. Agregar EN_ESPERA_PROVEEDOR el proximo semestre es
// crear su clase y agregar una linea aqui.
public final class FabricaEstados {

    private static final Map<String, Supplier<EstadoSolicitud>> ESTADOS = Map.of(
        EstadoPendiente.NOMBRE,    EstadoPendiente::new,
        EstadoEnAprobacion.NOMBRE, EstadoEnAprobacion::new,
        EstadoAprobada.NOMBRE,     EstadoAprobada::new,
        EstadoRechazada.NOMBRE,    EstadoRechazada::new,
        EstadoEjecutada.NOMBRE,    EstadoEjecutada::new,
        EstadoCancelada.NOMBRE,    EstadoCancelada::new
    );

    private FabricaEstados() {}

    public static EstadoSolicitud desde(String nombreEstado) {
        Supplier<EstadoSolicitud> constructor = ESTADOS.get(nombreEstado);
        if (constructor == null) {
            throw new IllegalArgumentException("Estado desconocido: " + nombreEstado);
        }
        return constructor.get();
    }
}
