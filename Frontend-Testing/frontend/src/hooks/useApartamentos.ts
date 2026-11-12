import { useEffect, useState } from 'react';
import type { ApartamentoResumen } from '../models/apartamento';
import { api } from '../services/api';
import { mensajeDeError } from '../utils/errores';

type Catalogo =
  | { estado: 'cargando' }
  | { estado: 'error'; mensaje: string }
  | { estado: 'listo'; apartamentos: ApartamentoResumen[] };

export function useApartamentos() {
  const [catalogo, setCatalogo] = useState<Catalogo>({ estado: 'cargando' });

  useEffect(() => {
    const solicitud = new AbortController();
    api.get<ApartamentoResumen[]>('/apartamentos', { signal: solicitud.signal })
      .then(({ data }) => setCatalogo({ estado: 'listo', apartamentos: data }))
      .catch((error: unknown) => {
        if (!solicitud.signal.aborted) setCatalogo({ estado: 'error', mensaje: mensajeDeError(error) });
      });
    return () => solicitud.abort();
  }, []);

  return catalogo;
}
