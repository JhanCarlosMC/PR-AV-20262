import axios from 'axios';
import { borrarSesion, EVENTO_SESION_EXPIRADA, leerSesion } from './sesion';

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL?.trim() || '/api',
  timeout: 10_000,
});

api.interceptors.request.use((config) => {
  const sesion = leerSesion();
  const esCatalogoPublico = config.method === 'get' && config.url === '/apartamentos';
  if (sesion && !esCatalogoPublico && !config.url?.startsWith('/auth/')) {
    config.headers.set('Authorization', `Bearer ${sesion.token}`);
  }
  return config;
});

api.interceptors.response.use(undefined, (error: unknown) => {
  if (axios.isAxiosError(error) && error.response?.status === 401) {
    const sesion = leerSesion();
    const autorizacion = error.config?.headers.get('Authorization');
    if (sesion && autorizacion === `Bearer ${sesion.token}`) {
      borrarSesion();
      window.dispatchEvent(new Event(EVENTO_SESION_EXPIRADA));
    }
  }
  return Promise.reject(error);
});
