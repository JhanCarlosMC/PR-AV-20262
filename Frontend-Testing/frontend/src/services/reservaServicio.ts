import type {
  CrearReservaRequest,
  EstadoReserva,
  ReservaDetalleResponse,
  ReservaResumenResponse,
} from '../models/reserva';
import { api } from './api';

export async function crearReserva(datos: CrearReservaRequest): Promise<ReservaDetalleResponse> {
  const { data } = await api.post<ReservaDetalleResponse>('/reservas', datos);
  return data;
}

export async function listarReservas(estado: EstadoReserva, signal: AbortSignal): Promise<ReservaResumenResponse[]> {
  const { data } = await api.get<ReservaResumenResponse[]>('/reservas', { params: { estado }, signal });
  return data;
}

export async function obtenerReserva(codigo: string, signal: AbortSignal): Promise<ReservaDetalleResponse> {
  const { data } = await api.get<ReservaDetalleResponse>(`/reservas/${encodeURIComponent(codigo)}`, { signal });
  return data;
}

export async function confirmarReserva(codigo: string): Promise<ReservaDetalleResponse> {
  const { data } = await api.put<ReservaDetalleResponse>(`/reservas/${encodeURIComponent(codigo)}/confirmar`);
  return data;
}

export async function cancelarReserva(codigo: string, motivo: string): Promise<ReservaDetalleResponse> {
  const { data } = await api.put<ReservaDetalleResponse>(
    `/reservas/${encodeURIComponent(codigo)}/cancelar`,
    { motivo },
  );
  return data;
}
