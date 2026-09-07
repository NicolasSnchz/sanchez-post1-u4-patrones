package com.universidad.compras.estado;

public class EstadoPendiente extends EstadoBase {

    public static final String NOMBRE = "PENDIENTE";

    @Override
    public String nombre() {
        return NOMBRE;
    }

    @Override
    public String aprobar(ContextoSolicitud contexto) {
        contexto.transicionarA(new EstadoEnAprobacion(), "La solicitud entro al circuito de aprobacion");
        return "En aprobacion";
    }

    @Override
    public String cancelar(ContextoSolicitud contexto) {
        contexto.transicionarA(new EstadoCancelada(), "El solicitante cancelo la solicitud antes de aprobarse");
        return "Cancelada";
    }
}
