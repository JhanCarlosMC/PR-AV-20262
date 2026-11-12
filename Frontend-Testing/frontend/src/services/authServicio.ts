import { api } from './api';
import type { TokenResponse, LoginRequest } from '../models/auth'

export async function login(credentials: LoginRequest): Promise<TokenResponse> {
    const respuesta = await api.post<TokenResponse>('/auth/login', {
        correo: credentials.username,
        contrasena: credentials.password,
    });
    return respuesta.data;
}