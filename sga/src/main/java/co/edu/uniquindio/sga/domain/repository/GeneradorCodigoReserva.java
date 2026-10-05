package co.edu.uniquindio.sga.domain.repository;

import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;

// Puerto del mismo tipo que un repositorio, aunque no calce en el molde obtener/guardar
public interface GeneradorCodigoReserva {

    CodigoReserva generar();
}
