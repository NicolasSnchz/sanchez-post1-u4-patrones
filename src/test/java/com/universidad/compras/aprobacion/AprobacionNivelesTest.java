package com.universidad.compras.aprobacion;

import com.universidad.compras.modelo.Solicitud;
import com.universidad.compras.notificacion.PublicadorCambioEstado;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AprobacionNivelesTest {

    private ServicioAprobacion servicio() {
        return new CadenaAprobacion(PublicadorCambioEstado.porDefecto());
    }

    @Test
    void solicitudDentroDeAutoridadDelSupervisorSeAprueba() {
        ServicioAprobacion servicio = servicio();
        Solicitud s = new Solicitud("S-001", "ana@udes.edu.co", 1500000, "MATERIAL_OFICINA", "CC-100");
        ResultadoAprobacion r = servicio.evaluar(s);
        assertTrue(r.isAprobada());
        assertEquals("Supervisor de \u00c1rea", r.getNivelResolutor());
    }

    @Test
    void solicitudQueSuperaAlSupervisorEscalaAlGerente() {
        ServicioAprobacion servicio = servicio();
        Solicitud s = new Solicitud("S-002", "luis@udes.edu.co", 6000000, "SOFTWARE", "CC-200");
        ResultadoAprobacion r = servicio.evaluar(s);
        assertTrue(r.isAprobada());
        assertEquals("Gerente de \u00c1rea", r.getNivelResolutor());
    }

    @Test
    void solicitudInternacionalPasaPorCumplimientoAntesDelNivelPorMonto() {
        ServicioAprobacion servicio = servicio();
        Solicitud s = new Solicitud("S-003", "gerencia@udes.edu.co", 1000000, "INTERNACIONAL", "CC-300");
        ResultadoAprobacion r = servicio.evaluar(s);
        assertEquals("Revisor de Cumplimiento Normativo", r.getNivelResolutor());
    }

    @Test
    void solicitudQueSuperaAlGerenteLlegaAlDirectorFinanciero() {
        ServicioAprobacion servicio = servicio();
        Solicitud s = new Solicitud("S-004", "rectoria@udes.edu.co", 50000000, "SOFTWARE", "CC-400");
        ResultadoAprobacion r = servicio.evaluar(s);
        assertTrue(r.isAprobada());
        assertEquals("Director Financiero", r.getNivelResolutor());
    }

    @Test
    void elNivelResolutorQuedaRegistradoEnLaSolicitud() {
        ServicioAprobacion servicio = servicio();
        Solicitud s = new Solicitud("S-005", "ana@udes.edu.co", 900000, "MATERIAL_OFICINA", "CC-100");
        servicio.evaluar(s);
        assertEquals("Supervisor de \u00c1rea", s.getNivelResolutor());
        assertEquals("APROBADA", s.getEstado());
    }
}
