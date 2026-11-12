import { useCallback, useEffect, useState } from 'react';
import { Link, Navigate, Outlet, Route, Routes, useLocation } from 'react-router';
import { Encabezado } from './components/Encabezado';
import type { TokenResponse } from './models/auth';
import DetalleApartamento from './pages/DetalleApartamento';
import DetalleReserva from './pages/DetalleReserva';
import Inicio from './pages/Inicio';
import ListaReservas from './pages/ListaReservas';
import Login from './pages/Login';
import NoEncontrada from './pages/NoEncontrada';
import NuevaReserva from './pages/NuevaReserva';
import Registro from './pages/Registro';
import {
  borrarSesion,
  EVENTO_SESION_EXPIRADA,
  guardarSesion,
  leerSesion,
  puedeGestionarReservas,
} from './services/sesion';

function RutaReservas({ sesion }: { sesion: TokenResponse | null }) {
  const { pathname, search } = useLocation();
  if (!sesion) return <Navigate to="/login" replace state={{ desde: pathname, busqueda: search }} />;
  if (!puedeGestionarReservas(sesion)) {
    return (
      <section>
        <h1>Acceso restringido</h1>
        <p>La gestión de reservas está disponible para recepción y administración.</p>
        <Link to="/">Volver al inicio</Link>
      </section>
    );
  }
  return <Outlet />;
}

export default function App() {
  const [sesion, setSesion] = useState(leerSesion);
  const { state } = useLocation();
  const desde = state?.desde;
  const destinoReservas = typeof desde === 'string' && /^\/reservas(?:\/[^/]+)?$/.test(desde)
    ? { pathname: desde, search: typeof state?.busqueda === 'string' ? state.busqueda : '' }
    : '/reservas';

  const salir = useCallback(() => {
    borrarSesion();
    setSesion(null);
  }, []);

  useEffect(() => {
    window.addEventListener(EVENTO_SESION_EXPIRADA, salir);
    return () => window.removeEventListener(EVENTO_SESION_EXPIRADA, salir);
  }, [salir]);

  useEffect(() => {
    if (!sesion) return;
    const demora = Math.max(0, Date.parse(sesion.expiraEn) - Date.now());
    const temporizador = window.setTimeout(salir, demora);
    return () => window.clearTimeout(temporizador);
  }, [sesion, salir]);

  function entrar(nuevaSesion: TokenResponse) {
    guardarSesion(nuevaSesion);
    setSesion(nuevaSesion);
  }

  return (
    <>
      <Encabezado sesion={sesion} alSalir={salir} />
      <main>
        <Routes>
          <Route path="/" element={<Inicio />} />
          <Route path="/login" element={
            sesion
              ? <Navigate to={puedeGestionarReservas(sesion) ? destinoReservas : '/'} replace />
              : <Login alIngresar={entrar} />
          } />
          <Route path="/registro" element={<Registro />} />
          <Route path="/apartamentos/:identificacion" element={<DetalleApartamento />} />
          <Route element={<RutaReservas sesion={sesion} />}>
            <Route path="/reservas" element={<ListaReservas />} />
            <Route path="/reservas/nueva" element={<NuevaReserva />} />
            <Route path="/reservas/:codigo" element={<DetalleReserva />} />
          </Route>
          <Route path="*" element={<NoEncontrada />} />
        </Routes>
      </main>
    </>
  );
}
