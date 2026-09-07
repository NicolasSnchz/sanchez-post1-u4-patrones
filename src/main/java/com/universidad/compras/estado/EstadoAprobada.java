package com.universidad.compras.estado;

public class EstadoAprobada extends EstadoBase {

    public static final String NOMBRE = "APROBADA";

    @Override
    public String nombre() {
        return NOMBRE;
    }

    @Override
    public String ejecutar(ContextoSolicitud contexto) {
        contexto.transicionarA(new EstadoEjecutada(), "Presupuesto reservado y orden de compra generada");
        return "Ejecutada";
    }

    @Override
    public String cancelar(ContextoSolicitud contexto) {
        contexto.transicionarA(new EstadoCancelada(), "Cancelada despues de aprobarse y antes de ejecutarse");
        return "Cancelada";
    }
}
