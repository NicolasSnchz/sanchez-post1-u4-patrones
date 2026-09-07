package com.universidad.compras.ejecucion;

import com.universidad.compras.modelo.Solicitud;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EjecucionSolicitudTest {

    private static final String PROVEEDOR = "Suministros Institucionales";

    @Test
    void ejecutarReservaPresupuestoYGeneraOrden() {
        Solicitud s = new Solicitud("S-010", "ana@udes.edu.co", 3000000, "SOFTWARE", "CC-100");
        s.setEstado("APROBADA");
        InvocadorEjecucion ejecutor = new InvocadorEjecucion();
        ejecutor.ejecutar(new ReservarPresupuestoComando(new PresupuestoService(), s));
        ejecutor.ejecutar(new GenerarOrdenCompraComando(new OrdenCompraService(), s, PROVEEDOR));
        assertEquals("EJECUTADA", s.getEstado());
    }

    @Test
    void deshacerSoloLaUltimaOperacionNoAfectaLaAnterior() {
        Solicitud s = new Solicitud("S-011", "luis@udes.edu.co", 4000000, "MATERIAL_OFICINA", "CC-200");
        s.setEstado("APROBADA");
        InvocadorEjecucion ejecutor = new InvocadorEjecucion();
        ComandoEjecucion reserva = new ReservarPresupuestoComando(new PresupuestoService(), s);
        ComandoEjecucion orden = new GenerarOrdenCompraComando(new OrdenCompraService(), s, PROVEEDOR);

        assertDoesNotThrow(() -> {
            ejecutor.ejecutar(reserva);
            ejecutor.ejecutar(orden);
            ejecutor.deshacerUltima(s.getId());
        });

        assertEquals("APROBADA", s.getEstado());
        assertEquals(1, ejecutor.operacionesAplicadas(s.getId()));
        assertEquals(2, ejecutor.historial(s.getId()).size());
    }

    @Test
    void elHistorialConservaTodasLasOperacionesNoSoloLaUltima() {
        Solicitud s = new Solicitud("S-012", "ana@udes.edu.co", 2000000, "SOFTWARE", "CC-300");
        s.setEstado("APROBADA");
        InvocadorEjecucion ejecutor = new InvocadorEjecucion();
        ejecutor.ejecutar(new ReservarPresupuestoComando(new PresupuestoService(), s));
        ejecutor.ejecutar(new GenerarOrdenCompraComando(new OrdenCompraService(), s, PROVEEDOR));

        assertEquals(2, ejecutor.historial(s.getId()).size());
        assertEquals(2, ejecutor.descripcionesHistorial(s.getId()).size());
    }

    @Test
    void cadaOperacionSeDeshaceDeFormaIndependiente() {
        Solicitud s = new Solicitud("S-013", "luis@udes.edu.co", 1000000, "MATERIAL_OFICINA", "CC-400");
        s.setEstado("APROBADA");
        InvocadorEjecucion ejecutor = new InvocadorEjecucion();
        ejecutor.ejecutar(new ReservarPresupuestoComando(new PresupuestoService(), s));
        ejecutor.ejecutar(new GenerarOrdenCompraComando(new OrdenCompraService(), s, PROVEEDOR));

        assertTrue(ejecutor.deshacer(s.getId(), 0));
        assertEquals("EJECUTADA", s.getEstado());
        assertEquals(1, ejecutor.operacionesAplicadas(s.getId()));
    }
}
