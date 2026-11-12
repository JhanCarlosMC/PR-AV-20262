import { z } from 'zod';
import { hoyISO } from '../utils/fechas';

const esquemaOcupante = z.object({
  // Misma condición que @DocumentoValido (Guía 08): solo letras y números
  documento: z
    .string()
    .min(1, 'Ingrese el documento')
    .regex(/^[A-Za-z0-9]+$/, 'El documento solo admite letras y números'),
  nombre: z.string().trim().min(1, 'Ingrese el nombre'),
  fechaNacimiento: z
    .iso.date('Ingrese la fecha de nacimiento')
    .refine((fecha) => fecha < hoyISO(), 'La fecha de nacimiento no puede ser futura'),
});

export const esquemaNuevaReserva = z.object({
    identificacionApartamento: z.string().trim().min(1, 'Indique el apartamento'),
    fechaEntrada: z.iso.date('Ingrese la fecha de entrada'),
    fechaSalida: z.iso.date('Ingrese la fecha de salida'),
    // input type="time" entrega "" cuando está vacío: se acepta vacío o una hora válida
    horaEstimadaLlegada: z.union([z.iso.time('Hora inválida'), z.literal('')]),
    ocupantes: z.array(esquemaOcupante).min(1, 'La reserva debe tener al menos un ocupante'),
  })
  // Validación entre dos campos. Es una ayuda para el usuario: la regla RN-03
  // sigue viviendo en Estancia y el backend responde 409 si se viola.
  .refine((datos) => datos.fechaSalida > datos.fechaEntrada, {
    error: 'La fecha de salida debe ser posterior a la de entrada',
    path: ['fechaSalida'],
  });

export type FormularioNuevaReserva = z.infer<typeof esquemaNuevaReserva>;