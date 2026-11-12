interface DatosAlojamiento {
  banos: number;
  descripcion: string;
  servicios: string[];
  enlace: string;
}

export const datosAlojamiento: Partial<Record<string, DatosAlojamiento>> = {
  'APT-301': {
    banos: 1,
    descripcion: 'Dos balcones con vistas a Salento y a la montaña. El anuncio incluye un espacio de trabajo con wifi.',
    servicios: ['Dos balcones', 'Vistas a la ciudad y a la montaña', 'Espacio de trabajo con wifi'],
    enlace: 'https://www.airbnb.com/rooms/607154228205912729',
  },
  'APT-302': {
    banos: 2,
    descripcion: 'Cocina, sala y comedor para compartir en familia, además de patio y balcón con vista a la montaña.',
    servicios: ['Cocina equipada', 'Sala y comedor', 'Patio y balcón', 'Agua caliente'],
    enlace: 'https://es.airbnb.com/rooms/692726244687089958',
  },
};
