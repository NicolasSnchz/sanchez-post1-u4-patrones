package com.universidad.compras.notificacion;

// Contrato unico que debe cumplir cualquier modulo que quiera reaccionar
// a un cambio de estado. Agregar una reaccion nueva es escribir una clase
// que implemente esta interfaz y registrarla en el publicador.
public interface SuscriptorEstado {
    void alCambiarEstado(EventoCambioEstado evento);
}
