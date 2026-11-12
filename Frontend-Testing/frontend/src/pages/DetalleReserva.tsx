import { useEffect, useState, type FormEvent } from 'react';
import { Link, useLocation, useParams } from 'react-router';
import { ESTADOS_RESERVA, type ReservaDetalleResponse } from '../models/reserva';
import { cancelarReserva, confirmarReserva, obtenerReserva } from '../services/reservaServicio';
import { mensajeDeError } from '../utils/errores';

type Resultado =
  | { reserva: ReservaDetalleResponse }
  | { error: string };

function DatosReserva({ codigo, creada }: { codigo: string; creada: boolean }) {
  const [resultado, setResultado] = useState<Resultado | null>(null);
  const [errorAccion, setErrorAccion] = useState('');
  const [mensaje, setMensaje] = useState(creada ? 'Reserva creada.' : '');
  const [ocupado, setOcupado] = useState(false);
  const [motivo, setMotivo] = useState('');

  useEffect(() => {
    const peticion = new AbortController();
    obtenerReserva(codigo, peticion.signal)
      .then((reserva) => setResultado({ reserva }))
      .catch((error: unknown) => {
        if (!peticion.signal.aborted) setResultado({ error: mensajeDeError(error) });
      });
    return () => peticion.abort();
  }, [codigo]);

  async function aplicar(accion: 'confirmar' | 'cancelar') {
    setErrorAccion('');
    setMensaje('');
    setOcupado(true);
    try {
      const reserva = accion === 'confirmar'
        ? await confirmarReserva(codigo)
        : await cancelarReserva(codigo, motivo.trim());
      setResultado({ reserva });
      setMensaje(accion === 'confirmar' ? 'Reserva confirmada.' : 'Reserva cancelada.');
    } catch (error) {
      setErrorAccion(mensajeDeError(error));
    } finally {
      setOcupado(false);
    }
  }

  function alCancelar(evento: FormEvent<HTMLFormElement>) {
    evento.preventDefault();
    if (motivo.trim().length < 5) {
      setErrorAccion('Ingrese un motivo de al menos 5 caracteres.');
      return;
    }
    void aplicar('cancelar');
  }

  if (!resultado) return <p role="status">Cargando reserva…</p>;
  if ('error' in resultado) return <p className="error" role="alert">{resultado.error}</p>;

  const { reserva } = resultado;
  const pendiente = reserva.estado === 'PENDIENTE';
  const cancelable = pendiente || reserva.estado === 'CONFIRMADA';
  const total = new Intl.NumberFormat('es-CO', {
    style: 'currency',
    currency: reserva.moneda,
    maximumFractionDigits: 0,
  }).format(reserva.valorTotal);

  return (
    <>
      <div className="fila">
        <h1>{reserva.codigo}</h1>
        <span className="estado">{ESTADOS_RESERVA[reserva.estado]}</span>
      </div>
      {mensaje && <p className="exito" role="status">{mensaje}</p>}
      <dl className="datos">
        <div><dt>Apartamento</dt><dd>{reserva.identificacionApartamento}</dd></div>
        <div><dt>Titular</dt><dd>{reserva.titular.nombre}</dd></div>
        <div><dt>Estancia</dt><dd>{reserva.fechaEntrada} a {reserva.fechaSalida}</dd></div>
        <div><dt>Llegada estimada</dt><dd>{reserva.horaEstimadaLlegada ?? 'Sin registrar'}</dd></div>
        <div><dt>Ocupantes</dt><dd>{reserva.totalOcupantes}</dd></div>
        <div><dt>Total</dt><dd>{total}</dd></div>
        {reserva.motivoCancelacion && <div><dt>Motivo de cancelación</dt><dd>{reserva.motivoCancelacion}</dd></div>}
      </dl>
      {pendiente && (
        reserva.horaEstimadaLlegada
          ? <button type="button" disabled={ocupado} onClick={() => void aplicar('confirmar')}>Confirmar reserva</button>
          : <p>Se requiere una hora estimada de llegada para confirmar la reserva.</p>
      )}
      {cancelable && (
        <form className="formulario-cancelar" onSubmit={alCancelar}>
          <label htmlFor="motivo">Motivo de cancelación</label>
          <textarea id="motivo" value={motivo} onChange={(evento) => setMotivo(evento.target.value)}
            required minLength={5} maxLength={300} rows={3} />
          <button className="peligro" type="submit" disabled={ocupado}>Cancelar reserva</button>
        </form>
      )}
      {errorAccion && <p className="error" role="alert">{errorAccion}</p>}
    </>
  );
}

export default function DetalleReserva() {
  const { codigo = '' } = useParams();
  const { state } = useLocation();
  return (
    <section className="panel">
      <Link to="/reservas">Volver a reservas</Link>
      <DatosReserva key={codigo} codigo={codigo} creada={state?.creada === true} />
    </section>
  );
}
