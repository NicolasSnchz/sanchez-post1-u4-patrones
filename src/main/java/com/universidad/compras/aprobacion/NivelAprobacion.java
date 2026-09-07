package com.universidad.compras.aprobacion;

import com.universidad.compras.modelo.Solicitud;

// Eslabon de la cadena de aprobacion. Cada nivel decide por si mismo si la
// solicitud entra en su competencia: si entra la resuelve, si no la pasa al
// siguiente. Ningun nivel sabe cuantos niveles hay ni quien va despues del
// siguiente, y el codigo que dispara la evaluacion no sabe nada de esto.
public abstract class NivelAprobacion {

    private NivelAprobacion siguiente;

    public NivelAprobacion enlazarCon(NivelAprobacion siguiente) {
        this.siguiente = siguiente;
        return siguiente;
    }

    public ResultadoAprobacion evaluar(Solicitud solicitud) {
        if (esDeMiCompetencia(solicitud)) {
            solicitud.setNivelResolutor(nombre());
            return resolver(solicitud);
        }
        if (siguiente != null) {
            return siguiente.evaluar(solicitud);
        }
        return new ResultadoAprobacion(false, "Sin resolutor",
            "Ningun nivel de aprobacion pudo resolver la solicitud " + solicitud.getId());
    }

    public abstract String nombre();

    protected abstract boolean esDeMiCompetencia(Solicitud solicitud);

    protected abstract ResultadoAprobacion resolver(Solicitud solicitud);
}
