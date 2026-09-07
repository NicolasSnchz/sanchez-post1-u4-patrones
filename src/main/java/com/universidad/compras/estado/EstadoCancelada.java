package com.universidad.compras.estado;

public class EstadoCancelada extends EstadoBase {

    public static final String NOMBRE = "CANCELADA";

    @Override
    public String nombre() {
        return NOMBRE;
    }
}
