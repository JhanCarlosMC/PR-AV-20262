package co.edu.uniquindio.sga.domain.entity;

import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;
import co.edu.uniquindio.sga.domain.valueobject.*;

import java.util.List;
import java.util.Objects;

public class Reserva {

    //Atributos
    private final CodigoReserva codigo; //Identificador
    private final CanalOrigen canalOrigen;
    private final FechaCreacion fechaCreacion;
    private final Ocupante titular;

    //Elementos cambiantes
    private EstadoReserva estado;
    private Apartamento apartamento;
    private Estancia estancia;
    private List<Ocupante> ocupantes;

    //Constructor
    private Reserva(CodigoReserva codigo, CanalOrigen canalOrigen,
                   FechaCreacion fechaCreacion, Ocupante titular,
                   Apartamento apartamento, Estancia estancia,
                   List<Ocupante> ocupantes) {

        this.codigo = codigo;
        this.canalOrigen = canalOrigen;
        this.fechaCreacion = fechaCreacion;
        this.titular = titular;
        this.apartamento = apartamento;
        this.estancia = estancia;
        this.ocupantes = ocupantes;

        estado = EstadoReserva.PENDIENTE;
    }

    public Reserva crear(CodigoReserva codigo, CanalOrigen canalOrigen,
                          FechaCreacion fechaCreacion, Ocupante titular,
                          Apartamento apartamento, Estancia estancia,
                          List<Ocupante> ocupantes){

        if (codigo == null){
            throw new ReglaDominioException("La reserva debe tener su codigo.");
        }
        if( canalOrigen == null){
            throw new ReglaDominioException("La reserva debe tener un canal de origen.");
        }
        if (ocupantes == null || ocupantes.isEmpty()){
            throw new ReglaDominioException("La reserva debe tener al menos un ocupante.");
        }
        //Terminar Validaciones de negocio

        return new Reserva(codigo, canalOrigen, fechaCreacion, titular,
                 apartamento, estancia, ocupantes);
    }

    //Reglas/Restricciones del negocio propios de reserva.

    //  ...

    public CodigoReserva getCodigo() {
        return codigo;
    }

    public CanalOrigen getCanalOrigen() {
        return canalOrigen;
    }

    public FechaCreacion getFechaCreacion() {
        return fechaCreacion;
    }

    public Ocupante getTitular() {
        return titular;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public Apartamento getApartamento() {
        return apartamento;
    }

    public Estancia getEstancia() {
        return estancia;
    }

    public List<Ocupante> getOcupantes() {
        return ocupantes;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Reserva reserva = (Reserva) o;
        return Objects.equals(codigo, reserva.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(codigo);
    }
}
