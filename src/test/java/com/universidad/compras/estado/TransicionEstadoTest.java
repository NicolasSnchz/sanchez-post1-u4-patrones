package com.universidad.compras.estado;

import com.universidad.compras.modelo.Solicitud;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TransicionEstadoTest {

    @Test
    void ejecutarUnaSolicitudAprobadaLaDejaEjecutada() {
        Solicitud s = new Solicitud("S-030", "luis@udes.edu.co", 3000000, "MATERIAL_OFICINA", "CC-200");
        s.setEstado("APROBADA");
        ContextoSolicitud contexto = new ContextoSolicitud(s);
        contexto.ejecutar();
        assertEquals("EJECUTADA", s.getEstado());
    }

    @Test
    void ejecutarUnaSolicitudPendienteSeRechazaSinCambiarElEstado() {
        Solicitud s = new Solicitud("S-031", "ana@udes.edu.co", 1000000, "SOFTWARE", "CC-100");
        ContextoSolicitud contexto = new ContextoSolicitud(s);
        String resultado = contexto.ejecutar();
        assertEquals("PENDIENTE", s.getEstado());
        assertTrue(resultado.startsWith("Error"));
    }

    @Test
    void unaSolicitudEjecutadaNoPuedeVolverAEjecutarse() {
        Solicitud s = new Solicitud("S-032", "ana@udes.edu.co", 1000000, "SOFTWARE", "CC-100");
        s.setEstado("EJECUTADA");
        ContextoSolicitud contexto = new ContextoSolicitud(s);
        String resultado = contexto.ejecutar();
        assertEquals("EJECUTADA", s.getEstado());
        assertTrue(resultado.startsWith("Error"));
    }

    @Test
    void elRecorridoCompletoLlevaDePendienteAEjecutada() {
        Solicitud s = new Solicitud("S-033", "luis@udes.edu.co", 1500000, "MATERIAL_OFICINA", "CC-200");
        ContextoSolicitud contexto = new ContextoSolicitud(s);
        contexto.aprobar();
        assertEquals("EN_APROBACION", s.getEstado());
        contexto.aprobar();
        assertEquals("APROBADA", s.getEstado());
        contexto.ejecutar();
        assertEquals("EJECUTADA", s.getEstado());
    }

    @Test
    void unaSolicitudCanceladaNoAdmiteMasOperaciones() {
        Solicitud s = new Solicitud("S-034", "ana@udes.edu.co", 500000, "SOFTWARE", "CC-300");
        ContextoSolicitud contexto = new ContextoSolicitud(s);
        contexto.cancelar();
        assertEquals("CANCELADA", s.getEstado());
        assertTrue(contexto.aprobar().startsWith("Error"));
        assertTrue(contexto.ejecutar().startsWith("Error"));
        assertEquals("CANCELADA", s.getEstado());
    }
}
