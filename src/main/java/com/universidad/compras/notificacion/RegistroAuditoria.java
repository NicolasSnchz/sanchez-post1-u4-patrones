package com.universidad.compras.notificacion;

import org.springframework.stereotype.Component;

@Component
public class RegistroAuditoria implements SuscriptorEstado {

    @Override
    public void alCambiarEstado(EventoCambioEstado evento) {
        ClientesNotificacion.registrarAuditoria(
            evento.getSolicitud().getId(),
            evento.getEstadoNuevo(),
            evento.getEstadoAnterior() + " -> " + evento.getEstadoNuevo() + ": " + evento.getDetalle()
        );
    }
}
