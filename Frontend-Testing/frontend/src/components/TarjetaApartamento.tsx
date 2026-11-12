import { Link } from 'react-router';
import { datosAlojamiento } from '../data/datosAlojamiento';
import type { ApartamentoResumen } from '../models/apartamento';

export function TarjetaApartamento({ apartamento }: { apartamento: ApartamentoResumen }) {
  const { nombre, identificacion, capacidad, dormitorios } = apartamento;
  const datos = datosAlojamiento[identificacion];
  return (
    <article className="marteja-alojamiento">
      <p className="marteja-capacidad">
        {capacidad} {capacidad === 1 ? 'huésped' : 'huéspedes'} · {dormitorios} {dormitorios === 1 ? 'habitación' : 'habitaciones'}
        {datos && ` · ${datos.banos} ${datos.banos === 1 ? 'baño' : 'baños'}`}
      </p>
      <h3>{nombre}</h3>
      {datos && <p>{datos.descripcion}</p>}
      <Link to={`/apartamentos/${encodeURIComponent(identificacion)}`} aria-label={`Ver detalle de ${nombre}`}>
        Ver detalle <span aria-hidden="true">→</span>
      </Link>
    </article>
  );
}
