import { z } from 'zod';

export const esquemaLogin = z.object({
    username: z.string().trim().pipe(z.email('Ingrese un correo válido')),
    password: z.string().min(1, 'Ingrese su contraseña'),
});

export type FormularioLogin = z.infer<typeof esquemaLogin>;