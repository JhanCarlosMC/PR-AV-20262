import axios from 'axios';
import type { ErrorResponse } from '../models/error';

/**
 * Traduce cualquier error de una petición a un mensaje para el usuario.
 * Si el backend envió un ErrorResponse (Guía 08), se usa su mensaje de negocio.
 */
export function mensajeDeError(error: unknown): string {
  if (!axios.isAxiosError<ErrorResponse>(error)) {
    return 'Ocurrió un error inesperado.';
  }
  if (!error.response) {
    // Sin respuesta: el backend está apagado, la URL está mal o CORS bloqueó la petición
    return 'No fue posible conectar con el servidor.';
  }
  const { status, data } = error.response;
  if (data?.mensaje) {
    return data.mensaje;
  }
  if (status === 403) {
    return 'No tiene permisos para realizar esta acción.';
  }
  return `Error inesperado del servidor (${status}).`;
}

/** Código HTTP de la respuesta, o undefined si no hubo respuesta. */
export function estadoHttp(error: unknown): number | undefined {
  return axios.isAxiosError(error) ? error.response?.status : undefined;
}

/**
 * Errores de campo de un 400 de Bean Validation, con el nombre de campo que usa
 * React Hook Form: "ocupantes[1].documento" → "ocupantes.1.documento".
 */
export function erroresDeCampo(error: unknown): Record<string, string> {
  if (!axios.isAxiosError<ErrorResponse>(error) || !error.response?.data?.errores) {
    return {};
  }
  const resultado: Record<string, string> = {};
  for (const [campo, mensaje] of Object.entries(error.response.data.errores)) {
    resultado[campo.replace(/\[(\d+)\]/g, '.$1')] = mensaje;
  }
  return resultado;
}