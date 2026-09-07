package com.universidad.compras.notificacion;

import com.universidad.compras.modelo.Solicitud;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NotificacionEstadoTest {

    // Cuarto suscriptor de prueba: se escribe aparte y se registra desde
    // afuera, sin modificar PublicadorCambioEstado.
    static class ColectorDePrueba implements SuscriptorEstado {
        final List<String> recibidos = new ArrayList<>();

        @Override
        public void alCambiarEstado(EventoCambioEstado evento) {
            recibidos.add(evento.getSolicitud().getId() + ":" + evento.getEstadoNuevo());
        }
    }

    @Test
    void cambiarEstadoDisparaLasTresReaccionesSinLanzarExcepcion() {
        Solicitud s = new Solicitud("S-020", "ana@udes.edu.co", 2500000, "SOFTWARE", "CC-100");
        PublicadorCambioEstado mecanismo = PublicadorCambioEstado.porDefecto();
        assertEquals(3, mecanismo.cantidadSuscriptores());
        assertDoesNotThrow(() -> mecanismo.cambiarEstado(s, "APROBADA", "Aprobada por el supervisor"));
        assertEquals("APROBADA", s.getEstado());
    }

    @Test
    void agregarUnCuartoSuscriptorDePruebaNoRequiereModificarElMecanismo() {
        Solicitud s = new Solicitud("S-021", "luis@udes.edu.co", 1200000, "MATERIAL_OFICINA", "CC-200");
        PublicadorCambioEstado mecanismo = PublicadorCambioEstado.porDefecto();
        ColectorDePrueba colector = new ColectorDePrueba();

        assertDoesNotThrow(() -> {
            mecanismo.suscribir(colector);
            mecanismo.cambiarEstado(s, "EJECUTADA", "Orden de compra generada");
        });

        assertEquals(4, mecanismo.cantidadSuscriptores());
        assertEquals(1, colector.recibidos.size());
        assertEquals("S-021:EJECUTADA", colector.recibidos.get(0));
    }

    @Test
    void elEventoConservaElEstadoAnteriorYElNuevo() {
        Solicitud s = new Solicitud("S-022", "ana@udes.edu.co", 800000, "SOFTWARE", "CC-300");
        PublicadorCambioEstado mecanismo = new PublicadorCambioEstado(List.of());
        List<EventoCambioEstado> eventos = new ArrayList<>();
        mecanismo.suscribir(eventos::add);

        mecanismo.cambiarEstado(s, "APROBADA", "Dentro de la autoridad del supervisor");

        assertEquals(1, eventos.size());
        assertEquals("PENDIENTE", eventos.get(0).getEstadoAnterior());
        assertEquals("APROBADA", eventos.get(0).getEstadoNuevo());
    }
}
