package com.universidad.compras.estado;

public class EstadoEjecutada extends EstadoBase {

    public static final String NOMBRE = "EJECUTADA";

    @Override
    public String nombre() {
        return NOMBRE;
    }
}
