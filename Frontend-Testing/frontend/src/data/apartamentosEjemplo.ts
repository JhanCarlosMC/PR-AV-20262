import type { ApartamentoResumen } from '../models/apartamento.ts';

// Datos de ejemplo mientras el frontend no consulta el backend.
// Reemplácelos por los apartamentos de su Ficha del Alojamiento (Anexo A).
export const apartamentosEjemplo: ApartamentoResumen[] = [
  { identificacion: 'APT-101', nombre: 'Guadua', dormitorios: 1, capacidad: 2 },
  { identificacion: 'APT-201', nombre: 'Palma de Cera', dormitorios: 2, capacidad: 4 },
  { identificacion: 'APT-301', nombre: 'Cocora', dormitorios: 3, capacidad: 6 },
];