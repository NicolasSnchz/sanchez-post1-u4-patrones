package com.universidad.compras.aprobacion;

import com.universidad.compras.modelo.Solicitud;

// Nivel adicional anunciado por el equipo de Compras. Su competencia no es
// un rango de monto sino una categoria: revisa las solicitudes
// INTERNACIONAL y deja pasar todo lo demas al nivel que corresponda.
public class RevisorCumplimientoNormativo extends NivelAprobacion {

    public static final String NOMBRE = "Revisor de Cumplimiento Normativo";
    public static final String CATEGORIA = "INTERNACIONAL";

    @Override
    public String nombre() {
        return NOMBRE;
    }

    @Override
    protected boolean esDeMiCompetencia(Solicitud solicitud) {
        return CATEGORIA.equals(solicitud.getCategoria());
    }

    @Override
    protected ResultadoAprobacion resolver(Solicitud solicitud) {
        return new ResultadoAprobacion(true, NOMBRE,
            "Compra internacional habilitada tras la revision de cumplimiento normativo");
    }
}
