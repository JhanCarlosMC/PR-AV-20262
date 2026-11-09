import { Link } from 'react-router';

interface EncabezadoProps {
  nombreAlojamiento: string;
}

export function Encabezado({ nombreAlojamiento }: EncabezadoProps) {
  return (
    <header>
      <p>{nombreAlojamiento}</p>
      <nav>
        <ul>
          <li>
            <Link to="/">Inicio</Link>
            </li>
          <li>
            <Link to="/login">Ingresar</Link>
            </li>
          <li>
            <Link to="/registro">Registrarse</Link>
            </li>
        </ul>
      </nav>
    </header>
  );
}