/**
 * Fecha de hoy como "AAAA-MM-DD" en la hora LOCAL del navegador.
 * No use new Date().toISOString(): devuelve la fecha en UTC, y en Colombia
 * (UTC-5) a partir de las 7 p. m. ya es "mañana" en UTC.
 */
export function hoyISO(): string {
  const hoy = new Date();
  const mes = String(hoy.getMonth() + 1).padStart(2, '0');
  const dia = String(hoy.getDate()).padStart(2, '0');
  return `${hoy.getFullYear()}-${mes}-${dia}`;
}