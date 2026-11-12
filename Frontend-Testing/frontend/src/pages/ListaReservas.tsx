import { useEffect, useState } from 'react';
import { Link } from 'react-router';
import { ESTADOS_RESERVA, type EstadoReserva, type ReservaResumenResponse } from '../models/reserva';
import { listarReservas } from '../services/reservaServicio';
import { mensajeDeError } from '../utils/errores';

type Resultado =
  | { reservas: ReservaResumenResponse[] }
  | { error: string };

function Resultados({ estado }: { estado: EstadoReserva }) {
  const [resultado, setResultado] = useState<Resultado | null>(null);

  useEffect(() => {
    const peticion = new AbortController();
    listarReservas(estado, peticion.signal)
      .then((reservas) => setResultado({ reservas }))
      .catch((error: unknown) => {
        if (!peticion.signal.aborted) setResultado({ error: mensajeDeError(error) });
      });
    return () => peticion.abort();
  }, [estado]);

  if (!resultado) return <p role="status">Cargando reservas…</p>;
  if ('error' in resultado) return <p className="error" role="alert">{resultado.error}</p>;
  if (!resultado.reservas.length) return <p role="status">No hay reservas en este estado.</p>;

  return (
    <ul className="listado">
      {resultado.reservas.map((reserva) => (
        <li key={reserva.codigo} className="tarjeta fila">
          <div>
            <h2><Link to={`/reservas/${encodeURIComponent(reserva.codigo)}`}>{reserva.codigo}</Link></h2>
            <p>{reserva.nombreTitular} · {reserva.identificacionApartamento}</p>
          </div>
          <p>{reserva.fechaEntrada} a {reserva.fechaSalida}</p>
        </li>
      ))}
    </ul>
  );
}

export default function ListaReservas() {
  const [estado, setEstado] = useState<EstadoReserva>('PENDIENTE');
  const [intento, setIntento] = useState(0);

  return (
    <section className="panel">
      <div className="fila">
        <h1>Reservas</h1>
        <Link className="boton" to="/reservas/nueva">Nueva reserva</Link>
      </div>
      <div className="fila filtro">
        <label htmlFor="estado">Estado</label>
        <select id="estado" value={estado} onChange={(evento) => setEstado(evento.target.value as EstadoReserva)}>
          {Object.entries(ESTADOS_RESERVA).map(([valor, etiqueta]) => (
            <option key={valor} value={valor}>{etiqueta}</option>
          ))}
        </select>
        <button className="secundario" type="button" onClick={() => setIntento((anterior) => anterior + 1)}>
          Actualizar
        </button>
      </div>
      <Resultados key={`${estado}-${intento}`} estado={estado} />
    </section>
  );
}
