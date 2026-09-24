package co.edu.uniquindio.sga.domain.valueobject;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

//Test para un objeto de valor
class IdentificacionApartamentoTest {

    @Test
    void verificarDosIdentificadoresConElMismoValorSonIguales(){
        IdentificacionApartamento idUno = new IdentificacionApartamento("apto:01");
        IdentificacionApartamento idDos = new IdentificacionApartamento("APTO:01");

        assertEquals(idUno,idDos);
        assertEquals(idUno.hashCode(),idDos.hashCode());
    }

    @Test
    void normalizarAMayusculas(){
        IdentificacionApartamento idUno = new IdentificacionApartamento("apto:01");

        assertEquals("APTO:01",idUno.valor());
    }

}