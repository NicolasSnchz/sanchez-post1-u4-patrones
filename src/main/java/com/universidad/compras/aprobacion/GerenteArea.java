package com.universidad.compras.aprobacion;

import com.universidad.compras.modelo.Solicitud;

public class GerenteArea extends NivelAprobacion {

    public static final double LIMITE = 10_000_000;
    public static final String NOMBRE = "Gerente de \u00c1rea";

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
            "Aprobada por el gerente de area: el monto esta dentro de su autoridad de $" + LIMITE);
    }
}
