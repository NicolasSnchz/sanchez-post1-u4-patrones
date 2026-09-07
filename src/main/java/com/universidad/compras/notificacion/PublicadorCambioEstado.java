package com.universidad.compras.notificacion;

import com.universidad.compras.modelo.Solicitud;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

// Unico punto por el que pasa un cambio de estado de una Solicitud.
// No conoce a ningun modulo concreto: solo mantiene la lista de
// suscriptores registrados y les avisa. Agregar una reaccion nueva no
// obliga a tocar esta clase ni a los que disparan el cambio.
@Component
public class PublicadorCambioEstado {

    private final List<SuscriptorEstado> suscriptores = new ArrayList<>();

    public PublicadorCambioEstado(List<SuscriptorEstado> suscriptores) {
        this.suscriptores.addAll(suscriptores);
    }

    // Configuracion por defecto usada fuera del contenedor de Spring.
    public static PublicadorCambioEstado porDefecto() {
        return new PublicadorCambioEstado(List.of(
            new NotificadorCorreo(),
            new ActualizadorContabilidad(),
            new RegistroAuditoria()
        ));
    }

    public void suscribir(SuscriptorEstado suscriptor) {
        suscriptores.add(suscriptor);
    }

    public int cantidadSuscriptores() {
        return suscriptores.size();
    }

    // El estado se cambia siempre por aqui: primero se actualiza la
    // solicitud y despues se avisa a todos los suscriptores registrados.
    public void cambiarEstado(Solicitud solicitud, String estadoNuevo, String detalle) {
        String estadoAnterior = solicitud.getEstado();
        solicitud.setEstado(estadoNuevo);
        EventoCambioEstado evento = new EventoCambioEstado(solicitud, estadoAnterior, estadoNuevo, detalle);
        for (SuscriptorEstado suscriptor : new ArrayList<>(suscriptores)) {
            suscriptor.alCambiarEstado(evento);
        }
    }
}
