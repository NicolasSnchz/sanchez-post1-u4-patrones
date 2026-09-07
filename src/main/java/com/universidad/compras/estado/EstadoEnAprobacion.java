package com.universidad.compras.estado;

public class EstadoEnAprobacion extends EstadoBase {

    public static final String NOMBRE = "EN_APROBACION";

    @Override
    public String nombre() {
        return NOMBRE;
    }

    @Override
    public String aprobar(ContextoSolicitud contexto) {
        contexto.transicionarA(new EstadoAprobada(), "Aprobada por el nivel resolutor correspondiente");
        return "Aprobada";
    }

    @Override
    public String rechazar(ContextoSolicitud contexto) {
        contexto.transicionarA(new EstadoRechazada(), "Rechazada durante la evaluacion por niveles");
        return "Rechazada";
    }

    @Override
    public String cancelar(ContextoSolicitud contexto) {
        contexto.transicionarA(new EstadoCancelada(), "Cancelada mientras estaba en aprobacion");
        return "Cancelada";
    }
}
