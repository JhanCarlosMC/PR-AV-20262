// Espejo del ErrorResponse del backend (Guía 08, 7.1): la forma de TODOS los errores de la API.
export interface ErrorResponse {
  timestamp: string;
  status: number;
  error: string;
  mensaje: string;                         // mensaje con significado de negocio
  path: string;
  errores: Record<string, string> | null;  // solo en un 400 de Bean Validation: campo → mensaje
}