import { Link, useParams } from 'react-router';
import { datosAlojamiento } from '../data/datosAlojamiento';
import { useApartamentos } from '../hooks/useApartamentos';
import './Inicio.css';

export default function DetalleApartamento() {
  const { identificacion } = useParams();
  const catalogo = useApartamentos();
  const apartamento = catalogo.estado === 'listo'
    ? catalogo.apartamentos.find((opcion) => opcion.identificacion === identificacion)
    : undefined;
  const datos = apartamento && datosAlojamiento[apartamento.identificacion];

  return (
    <section className="inicio-publico marteja-detalle" aria-label="Detalle del apartamento">
      <Link to="/#alojamientos">Volver a apartamentos</Link>
      {catalogo.estado === 'cargando' && <p role="status">Cargando apartamento…</p>}
      {catalogo.estado === 'error' && <p role="alert">{catalogo.mensaje}</p>}
      {catalogo.estado === 'listo' && !apartamento && <h1>Apartamento no encontrado</h1>}
      {apartamento && (
        <>
          <div className="marteja-detalle-portada">
            <p className="marteja-lugar">La Marteja · Salento, Quindío</p>
            <h1>{apartamento.nombre}</h1>
            {datos && <p>{datos.descripcion}</p>}
          </div>
          <dl className="datos marteja-detalle-datos">
            <div><dt>Capacidad</dt><dd>{apartamento.capacidad} huéspedes</dd></div>
            <div><dt>Dormitorios</dt><dd>{apartamento.dormitorios}</dd></div>
            {datos && <div><dt>Baños</dt><dd>{datos.banos}</dd></div>}
          </dl>
          {datos && (
            <section className="marteja-detalle-servicios">
              <h2>Servicios del apartamento</h2>
              <ul>{datos.servicios.map((servicio) => <li key={servicio}>{servicio}</li>)}</ul>
            </section>
          )}
          <div className="marteja-acciones">
            <Link className="marteja-boton" to={`/reservas/nueva?apartamento=${encodeURIComponent(apartamento.identificacion)}`}>
              Crear reserva en recepción <span aria-hidden="true">→</span>
            </Link>
            {datos && <a href={datos.enlace}>Ver fotos y disponibilidad en Airbnb <span aria-hidden="true">↗</span></a>}
          </div>
        </>
      )}
    </section>
  );
}
