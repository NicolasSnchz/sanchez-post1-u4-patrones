package com.universidad.compras.aprobacion;

import com.universidad.compras.modelo.Solicitud;

public class SupervisorArea extends NivelAprobacion {

    public static final double LIMITE = 2_000_000;
    public static final String NOMBRE = "Supervisor de \u00c1rea";

    @Override
    public String nombre() {
        return NOMBRE;
    }

    @Override
    protected boolean esDeMiCompetencia(Solicitud solicitud) {
        return solicitud.getMonto() <= LIMITE;
    }

    @Override
    protected ResultadoAprobacion resolver(Solicitud solicitud) {
        return new ResultadoAprobacion(true, NOMBRE,
            "Aprobada por el supervisor: el monto esta dentro de su autoridad de $" + LIMITE);
    }
}
