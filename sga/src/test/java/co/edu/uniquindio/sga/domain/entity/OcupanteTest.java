package co.edu.uniquindio.sga.domain.entity;

import co.edu.uniquindio.sga.domain.valueobject.DocumentoIdentidad;
import co.edu.uniquindio.sga.domain.valueobject.Estancia;
import co.edu.uniquindio.sga.domain.valueobject.UmbralEdadFacturable;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

    class OcupanteTest {

        /** RN-06: un ocupante es facturable si a la fecha de entrada alcanza el umbral configurado. */
        @Test
        void esFacturableSiAlcanzaUmbral(){
            Ocupante ocupanteNuevo = new Ocupante(
                    new DocumentoIdentidad("CC10123567"),
                    "Jhan Carlos Martinez Ceballos",
                    LocalDate.of(2001,01,13)
            );

            Estancia estanciaNueva = new Estancia(
                    LocalDate.of(2026, 9, 21),
                    LocalDate.of(2026, 9, 23));

            boolean facturable = ocupanteNuevo.esFacturableEn(estanciaNueva, new UmbralEdadFacturable(18));

            assertTrue(facturable);
        }
    }