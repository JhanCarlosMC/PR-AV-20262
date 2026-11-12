import { Link, NavLink, useNavigate } from 'react-router';
import type { TokenResponse } from '../models/auth';
import { puedeGestionarReservas } from '../services/sesion';

interface EncabezadoProps {
  sesion: TokenResponse | null;
  alSalir: () => void;
}

export function Encabezado({ sesion, alSalir }: EncabezadoProps) {
  const navegar = useNavigate();

  return (
    <header className="encabezado">
      <Link className="marca" to="/">ApartaHotel La Marteja</Link>
      <nav aria-label="Navegación principal">
        <NavLink to="/">Inicio</NavLink>
        {puedeGestionarReservas(sesion) && <NavLink to="/reservas">Reservas</NavLink>}
        {sesion ? (
          <button className="secundario" type="button" onClick={() => {
            alSalir();
            navegar('/login', { replace: true });
          }}>
            Cerrar sesión
          </button>
        ) : <NavLink to="/login">Ingresar</NavLink>}
      </nav>
    </header>
  );
}
