package com.universidad.compras.aprobacion;

import com.universidad.compras.modelo.Solicitud;

public class DirectorFinanciero extends NivelAprobacion {

    public static final String NOMBRE = "Director Financiero";

    @Override
    public String nombre() {
        return NOMBRE;
    }

    // Ultimo eslabon: no tiene limite superior, por lo que siempre resuelve
    // lo que le llega y la cadena nunca se queda sin resolutor.
    @Override
    protected boolean esDeMiCompetencia(Solicitud solicitud) {
        return true;
    }

    @Override
    protected ResultadoAprobacion resolver(Solicitud solicitud) {
        return new ResultadoAprobacion(true, NOMBRE,
            "Aprobada por el director financiero, sin limite superior de monto");
    }
}
