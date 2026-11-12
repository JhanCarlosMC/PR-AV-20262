export interface LoginRequest {
    username: string;
    password: string;
}

export interface TokenResponse {
    token: string;
    tipo: string;
    expiraEn: string;
    roles: Rol[];
}

export type Rol = 'HUESPED' | 'RECEPCIONISTA' | 'ADMINISTRADOR' | 'PERSONAL_SERVICIO';
