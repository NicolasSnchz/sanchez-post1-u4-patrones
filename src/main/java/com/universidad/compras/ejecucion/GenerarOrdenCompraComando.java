package com.universidad.compras.ejecucion;

import com.universidad.compras.modelo.Solicitud;
import com.universidad.compras.notificacion.PublicadorCambioEstado;

public class GenerarOrdenCompraComando implements ComandoEjecucion {

    private final OrdenCompraService ordenCompraService;
    private final PublicadorCambioEstado publicador;
    private final Solicitud solicitud;
    private final String proveedor;

    private String numeroOrden;
    private String estadoPrevio;

    public GenerarOrdenCompraComando(OrdenCompraService ordenCompraService, Solicitud solicitud, String proveedor) {
        this(ordenCompraService, solicitud, proveedor, PublicadorCambioEstado.porDefecto());
    }

    public GenerarOrdenCompraComando(OrdenCompraService ordenCompraService, Solicitud solicitud,
                                     String proveedor, PublicadorCambioEstado publicador) {
        this.ordenCompraService = ordenCompraService;
        this.solicitud = solicitud;
        this.proveedor = proveedor;
        this.publicador = publicador;
    }

    @Override
    public void ejecutar() {
        estadoPrevio = solicitud.getEstado();
        numeroOrden = ordenCompraService.generar(solicitud.getId(), proveedor);
        publicador.cambiarEstado(solicitud, "EJECUTADA", "Orden de compra " + numeroOrden + " generada");
    }

    @Override
    public void deshacer() {
        if (numeroOrden != null) {
            ordenCompraService.cancelar(numeroOrden);
            publicador.cambiarEstado(solicitud, estadoPrevio, "Orden de compra " + numeroOrden + " cancelada");
            numeroOrden = null;
        }
    }

    @Override
    public String descripcion() {
        return "Generacion de orden de compra para el proveedor " + proveedor;
    }

    @Override
    public Solicitud getSolicitud() {
        return solicitud;
    }

    public String getNumeroOrden() {
        return numeroOrden;
    }
}
