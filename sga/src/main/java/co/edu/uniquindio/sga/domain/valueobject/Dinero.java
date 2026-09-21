package co.edu.uniquindio.sga.domain.valueobject;

import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;

import java.math.BigDecimal;
import java.math.RoundingMode;

// Prohibido float/double para dinero: toda cifra monetaria del dominio pasa por este objeto de valor
public record Dinero(BigDecimal valor) {

    public static final Dinero CERO = new Dinero(BigDecimal.ZERO);

    public Dinero {
        if (valor == null) {
            throw new ReglaDominioException("El valor monetario es obligatorio.");
        }
        if (valor.signum() < 0) {
            throw new ReglaDominioException("El valor monetario no puede ser negativo.");
        }
        valor = valor.setScale(2, RoundingMode.HALF_UP);
    }

    public Dinero sumar(Dinero otro) {
        return new Dinero(valor.add(otro.valor));
    }

    public Dinero restar(Dinero otro) {
        return new Dinero(valor.subtract(otro.valor));
    }

    public Dinero multiplicarPor(int factor) {
        return new Dinero(valor.multiply(BigDecimal.valueOf(factor)));
    }

    public boolean esCero() {
        return valor.signum() == 0;
    }

    public boolean esMayorQue(Dinero otro) {
        return valor.compareTo(otro.valor) > 0;
    }
}
