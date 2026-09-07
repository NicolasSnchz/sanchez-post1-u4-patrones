package com.universidad.compras.aprobacion;

import com.universidad.compras.modelo.Solicitud;
import com.universidad.compras.notificacion.PublicadorCambioEstado;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

// Implementacion de ServicioAprobacion que arma la cadena de niveles y le
// entrega la solicitud al primero. Es el unico lugar del sistema donde se
// define que niveles existen y en que orden se consultan: agregar, quitar o
// reordenar un nivel se hace en el metodo nivelesPara, sin tocar
// ControladorSolicitudes ni los demas niveles.
@Service
public class CadenaAprobacion implements ServicioAprobacion {

    private final PublicadorCambioEstado publicador;

    public CadenaAprobacion(PublicadorCambioEstado publicador) {
        this.publicador = publicador;
    }

    @Override
    public ResultadoAprobacion evaluar(Solicitud solicitud) {
        publicador.cambiarEstado(solicitud, "EN_APROBACION",
            "Solicitud radicada por " + solicitud.getSolicitanteEmail());

        ResultadoAprobacion resultado = primerNivel(solicitud).evaluar(solicitud);

        publicador.cambiarEstado(solicitud, resultado.isAprobada() ? "APROBADA" : "RECHAZADA",
            resultado.getDetalle());
        return resultado;
    }

    private NivelAprobacion primerNivel(Solicitud solicitud) {
        List<NivelAprobacion> niveles = nivelesPara(solicitud);
        for (int i = 0; i < niveles.size() - 1; i++) {
            niveles.get(i).enlazarCon(niveles.get(i + 1));
        }
        return niveles.get(0);
    }

    private List<NivelAprobacion> nivelesPara(Solicitud solicitud) {
        List<NivelAprobacion> niveles = new ArrayList<>();
        if (RevisorCumplimientoNormativo.CATEGORIA.equals(solicitud.getCategoria())) {
            niveles.add(new RevisorCumplimientoNormativo());
        }
        niveles.add(new SupervisorArea());
        niveles.add(new GerenteArea());
        niveles.add(new DirectorFinanciero());
        return niveles;
    }
}
