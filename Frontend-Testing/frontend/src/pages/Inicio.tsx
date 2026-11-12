import { useEffect } from 'react';
import { useLocation } from 'react-router';
import { TarjetaApartamento } from '../components/TarjetaApartamento';
import { useApartamentos } from '../hooks/useApartamentos';
import './Inicio.css';

const booking = 'https://www.booking.com/hotel/co/apartahotel-la-marteja-salento.es.html';
const mapa = 'https://www.google.com/maps/search/?api=1&query=Calle+7+%232-59%2C+Salento%2C+Quind%C3%ADo';
const fotos = import.meta.env.BASE_URL + 'salento/';

export default function Inicio() {
  const catalogo = useApartamentos();
  const { hash } = useLocation();

  useEffect(() => {
    if (hash === '#alojamientos') document.getElementById('alojamientos')?.scrollIntoView();
  }, [hash]);

  return (
    <div className="inicio-publico">
      <section className="marteja-portada" aria-labelledby="titulo-inicio">
        <div className="marteja-presentacion">
          <p className="marteja-lugar">Salento · Quindío, Colombia</p>
          <h1 id="titulo-inicio">Apartahotel<br />La Marteja</h1>
          <p className="marteja-bajada">
            Apartamentos con cocina y balcón, cerca del parque principal. Quédate en el pueblo y sal a recorrer el valle de Cocora.
          </p>
          <div className="marteja-acciones">
            <a className="marteja-boton" href="#alojamientos">Ver apartamentos <span aria-hidden="true">↓</span></a>
            <a className="marteja-enlace-claro" href={booking}>Consultar en Booking <span aria-hidden="true">↗</span></a>
          </div>
        </div>
        <figure className="marteja-foto-portada">
          <img src={fotos + 'cocora.jpg'} alt="Montañas verdes y palmas de cera en el valle de Cocora"
            width="1276" height="957" fetchPriority="high" />
          <figcaption>Valle de Cocora · Salento</figcaption>
        </figure>
      </section>

      <ul className="marteja-servicios" aria-label="Características del alojamiento">
        <li><strong>Cocina equipada</strong><span>Para preparar tus propias comidas</span></li>
        <li><strong>Balcón y terraza</strong><span>Vistas a la montaña según el apartamento</span></li>
        <li><strong>Wifi gratuito</strong><span>Para planear el día desde el alojamiento</span></li>
      </ul>

      <section className="marteja-apartamentos" id="alojamientos" aria-labelledby="titulo-apartamentos">
        <div className="marteja-encabezado-seccion">
          <h2 id="titulo-apartamentos">Dónde quedarte</h2>
          <p>Compara los apartamentos de La Marteja y abre el detalle para conocer sus servicios y opciones de reserva.</p>
        </div>
        {catalogo.estado === 'cargando' && <p role="status">Cargando apartamentos…</p>}
        {catalogo.estado === 'error' && <p role="alert">{catalogo.mensaje}</p>}
        {catalogo.estado === 'listo' && (
          catalogo.apartamentos.length === 0
            ? <p>No hay apartamentos publicados.</p>
            : <div className="marteja-tarjetas">
                {catalogo.apartamentos.map((apartamento) => (
                  <TarjetaApartamento key={apartamento.identificacion} apartamento={apartamento} />
                ))}
              </div>
        )}
      </section>

      <section className="marteja-ubicacion" aria-labelledby="titulo-ubicacion">
        <figure>
          <img src={fotos + 'calle.jpg'} alt="Fachadas tradicionales y balcones en una calle de Salento"
            width="1100" height="618" loading="lazy" />
          <figcaption>Calles de Salento</figcaption>
        </figure>
        <div>
          <h2 id="titulo-ubicacion">Encuéntranos en Salento</h2>
          <p>En el barrio Fundadores, cerca del parque principal y de la terminal de buses.</p>
          <address>Calle 7 #2–59 · Salento, Quindío</address>
          <a href={mapa}>Cómo llegar <span aria-hidden="true">↗</span></a>
        </div>
      </section>

      <footer className="marteja-creditos">
        Fotografías del destino.
        {' '}<a href="https://commons.wikimedia.org/wiki/File:Vista_al_valle_de_Cocora.jpg">Cocora por Ferferic</a>
        {' '}(<a href="https://creativecommons.org/licenses/by/4.0/">CC BY 4.0</a>)
        {' '}y <a href="https://commons.wikimedia.org/wiki/File:Calles_de_Salento_1.JPG">Salento por Sergiodbotero</a>
        {' '}(<a href="https://creativecommons.org/licenses/by-sa/4.0/">CC BY-SA 4.0</a>). Redimensionadas para la web.
      </footer>
    </div>
  );
}
