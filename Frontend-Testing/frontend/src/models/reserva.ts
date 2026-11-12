export interface OcupanteResponse {
    documento: string;
    fechaNacimiento: string;
    nombre: string;
}

export interface OcupanteRequest {
    documento: string;
    fechaNacimiento: string;
    nombre: string;
}

export type CanalOrigen = 'PORTAL'|'EXTERNO'|'DIRECTO'

export interface CrearReservaRequest {
    identificacionApartamento: string;
    fechaEntrada: string;
    fechaSalida: string;
    canalOrigen: CanalOrigen;
    horaEstimadaLlegada: string | null;
    titular: OcupanteRequest;
    ocupantes: OcupanteRequest[];
}

export const ESTADOS_RESERVA = {
  PENDIENTE: 'Pendiente',
  CONFIRMADA: 'Confirmada',
  EN_CURSO: 'En curso',
  FINALIZADA: 'Finalizada',
  NO_SHOW: 'No se presentó',
  CANCELADA: 'Cancelada',
} as const;

export type EstadoReserva = keyof typeof ESTADOS_RESERVA;

export interface ReservaResumenResponse {
  codigo: string;
  estado: EstadoReserva;
  identificacionApartamento: string;
  fechaEntrada: string;
  fechaSalida: string;
  nombreTitular: string;
  totalOcupantes: number;
  valorTotal: number;
}
// Espejo de: record ReservaDetalleResponse(...) (Guía 08, 4.2)
export interface ReservaDetalleResponse {
  codigo: string;
  estado: EstadoReserva;
  canalOrigen: CanalOrigen;
  identificacionApartamento: string;
  fechaEntrada: string;
  fechaSalida: string;
  noches: number;
  horaEstimadaLlegada: string | null;
  titular: OcupanteResponse;
  ocupantes: OcupanteResponse[];
  totalOcupantes: number;
  valorTotal: number;                    // pesos, sin decimales: long en Java
  moneda: string;                        // "COP"
  versionPoliticaCancelacion: string;
  fechaCreacion: string;
  motivoCancelacion: string | null;
}