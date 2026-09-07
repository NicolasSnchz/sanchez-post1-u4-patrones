package com.universidad.compras.estado;

public class EstadoRechazada extends EstadoBase {

    public static final String NOMBRE = "RECHAZADA";

    @Override
    public String nombre() {
        return NOMBRE;
    }
}
