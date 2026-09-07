package com.universidad.compras.ejecucion;

import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Dispara los comandos y guarda dos registros por solicitud: el historial
// completo de lo que se ejecuto (que nunca se borra, para poder
// inspeccionarlo despues) y la pila de operaciones todavia aplicadas, que es
// de donde se deshace. No sabe que hace cada comando por dentro.
@Service
public class InvocadorEjecucion {

    private final Map<String, List<ComandoEjecucion>> historial = new LinkedHashMap<>();
    private final Map<String, Deque<ComandoEjecucion>> aplicadas = new LinkedHashMap<>();

    public void ejecutar(ComandoEjecucion comando) {
        comando.ejecutar();
        String id = comando.getSolicitud().getId();
        historial.computeIfAbsent(id, k -> new ArrayList<>()).add(comando);
        aplicadas.computeIfAbsent(id, k -> new ArrayDeque<>()).addLast(comando);
    }

    // Deshace la ultima operacion todavia aplicada sobre la solicitud, sin
    // tocar las anteriores.
    public boolean deshacerUltima(String solicitudId) {
        Deque<ComandoEjecucion> pila = aplicadas.get(solicitudId);
        if (pila == null || pila.isEmpty()) {
            return false;
        }
        pila.pollLast().deshacer();
        return true;
    }

    // Deshace una operacion puntual del historial sin afectar a las demas.
    public boolean deshacer(String solicitudId, int indice) {
        List<ComandoEjecucion> registro = historial.get(solicitudId);
        if (registro == null || indice < 0 || indice >= registro.size()) {
            return false;
        }
        ComandoEjecucion comando = registro.get(indice);
        comando.deshacer();
        Deque<ComandoEjecucion> pila = aplicadas.get(solicitudId);
        if (pila != null) {
            pila.remove(comando);
        }
        return true;
    }

    public List<ComandoEjecucion> historial(String solicitudId) {
        return List.copyOf(historial.getOrDefault(solicitudId, List.of()));
    }

    public List<String> descripcionesHistorial(String solicitudId) {
        List<String> descripciones = new ArrayList<>();
        for (ComandoEjecucion comando : historial(solicitudId)) {
            descripciones.add(comando.descripcion());
        }
        return descripciones;
    }

    public int operacionesAplicadas(String solicitudId) {
        Deque<ComandoEjecucion> pila = aplicadas.get(solicitudId);
        return pila == null ? 0 : pila.size();
    }
}
