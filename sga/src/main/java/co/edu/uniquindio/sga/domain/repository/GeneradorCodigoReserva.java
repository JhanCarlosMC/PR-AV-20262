package co.edu.uniquindio.sga.domain.repository;

import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;

// Puerto del mismo tipo que un repositorio, aunque no calce en el molde obtener/guardar
/** Puerto del dominio: genera el código de la reserva (Guía 06) */
public interface GeneradorCodigoReserva {

    CodigoReserva generar();
}
