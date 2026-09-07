package com.universidad.compras.notificacion;

import org.springframework.stereotype.Component;

@Component
public class NotificadorCorreo implements SuscriptorEstado {

    @Override
    public void alCambiarEstado(EventoCambioEstado evento) {
        ClientesNotificacion.enviarCorreo(
            evento.getSolicitud().getSolicitanteEmail(),
            "Solicitud " + evento.getSolicitud().getId() + " ahora esta en estado " + evento.getEstadoNuevo(),
            evento.getDetalle()
        );
    }
}
