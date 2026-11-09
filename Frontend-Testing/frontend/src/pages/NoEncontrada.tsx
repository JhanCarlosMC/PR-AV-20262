import { Link } from 'react-router';

export default function NoEncontrada() {
  return (
    <section>
      <h1>Página no encontrada</h1>
      <Link to="/">Ir al inicio</Link>
    </section>
  );
}