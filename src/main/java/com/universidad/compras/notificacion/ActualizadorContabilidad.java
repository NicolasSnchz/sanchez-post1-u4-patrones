package com.universidad.compras.notificacion;

import org.springframework.stereotype.Component;

@Component
public class ActualizadorContabilidad implements SuscriptorEstado {

    @Override
    public void alCambiarEstado(EventoCambioEstado evento) {
        ClientesNotificacion.actualizarDashboardContabilidad(
            evento.getSolicitud().getId(),
            evento.getEstadoNuevo(),
            evento.getSolicitud().getMonto()
        );
    }
}
