import type { TokenResponse } from '../models/auth';

const CLAVE_SESION = 'sga.sesion';
export const EVENTO_SESION_EXPIRADA = 'sga:sesion-expirada';

export function guardarSesion(sesion: TokenResponse): void {
  localStorage.setItem(CLAVE_SESION, JSON.stringify(sesion));
}

export function leerSesion(): TokenResponse | null {
  try {
    const texto = localStorage.getItem(CLAVE_SESION);
    if (!texto) return null;

    const sesion = JSON.parse(texto) as TokenResponse | null;
    if (sesion?.token && Array.isArray(sesion.roles) && Date.parse(sesion.expiraEn) > Date.now()) {
      return sesion;
    }
  } catch {
    return null;
  }
  return null;
}

export function borrarSesion(): void {
  localStorage.removeItem(CLAVE_SESION);
}

export function puedeGestionarReservas(sesion: TokenResponse | null): boolean {
  return sesion?.roles.some((rol) => rol === 'RECEPCIONISTA' || rol === 'ADMINISTRADOR') ?? false;
}
