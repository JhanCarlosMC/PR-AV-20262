import { zodResolver } from '@hookform/resolvers/zod';
import { Link, useNavigate, useSearchParams } from 'react-router';
import { useFieldArray, useForm, useWatch, type Path } from 'react-hook-form';
import { esquemaNuevaReserva, type FormularioNuevaReserva } from '../schema/reserva';
import { useApartamentos } from '../hooks/useApartamentos';
import type { ApartamentoResumen } from '../models/apartamento';
import type { CrearReservaRequest } from '../models/reserva';
import { crearReserva } from '../services/reservaServicio';
import { erroresDeCampo, mensajeDeError } from '../utils/errores';

const OCUPANTE_VACIO = { documento: '', nombre: '', fechaNacimiento: '' };

// Del formulario al DTO del backend. El primer ocupante es el titular, y la
// pantalla es de recepción: el canal de origen es DIRECTO (2.4).
function aSolicitud(datos: FormularioNuevaReserva): CrearReservaRequest {
  return {
    identificacionApartamento: datos.identificacionApartamento,
    fechaEntrada: datos.fechaEntrada,
    fechaSalida: datos.fechaSalida,
    horaEstimadaLlegada: datos.horaEstimadaLlegada || null,
    canalOrigen: 'DIRECTO',
    titular: datos.ocupantes[0],
    ocupantes: datos.ocupantes,
  };
}

export default function NuevaReserva() {
  const catalogo = useApartamentos();
  const [parametros] = useSearchParams();
  const codigo = parametros.get('apartamento') ?? '';
  const apartamentos = catalogo.estado === 'listo' ? catalogo.apartamentos : [];

  return (
    <section className="panel">
      <Link to="/reservas">Volver a reservas</Link>
      <h1>Nueva reserva (recepción)</h1>
      {catalogo.estado === 'cargando' && <p role="status">Cargando apartamentos…</p>}
      {catalogo.estado === 'error' && <p className="error" role="alert">{catalogo.mensaje}</p>}
      {catalogo.estado === 'listo' && apartamentos.length === 0 && <p>No hay apartamentos activos para reservar.</p>}
      {apartamentos.length > 0 && (
        <FormularioReserva key={codigo} apartamentos={apartamentos} codigoInicial={codigo} />
      )}
    </section>
  );
}

function FormularioReserva({ apartamentos, codigoInicial }: {
  apartamentos: ApartamentoResumen[];
  codigoInicial: string;
}) {
  const navegar = useNavigate();
  const codigoValido = apartamentos.some((apartamento) => apartamento.identificacion === codigoInicial);

  const {
    register, control, handleSubmit, setError,
    formState: { errors, isSubmitting },
  } = useForm<FormularioNuevaReserva>({
    resolver: zodResolver(esquemaNuevaReserva),
    mode: 'onTouched',
    defaultValues: {
      identificacionApartamento: codigoValido ? codigoInicial : '',
      fechaEntrada: '',
      fechaSalida: '',
      horaEstimadaLlegada: '',
      ocupantes: [OCUPANTE_VACIO],
    },
  });

  // useFieldArray administra la lista de ocupantes: agregar, quitar y registrar cada campo
  const { fields, append, remove } = useFieldArray({ control, name: 'ocupantes' });
  const codigoElegido = useWatch({ control, name: 'identificacionApartamento' });
  const apartamentoElegido = apartamentos.find((opcion) => opcion.identificacion === codigoElegido);
  const superaCapacidad = apartamentoElegido !== undefined && fields.length > apartamentoElegido.capacidad;

  async function alEnviar(datos: FormularioNuevaReserva) {
    if (superaCapacidad) return;
    try {
      const reserva = await crearReserva(aSolicitud(datos));
      navegar(`/reservas/${encodeURIComponent(reserva.codigo)}`, { replace: true, state: { creada: true } });
    } catch (error) {
      // 400: el backend dice qué campo falló; se muestra junto al campo
      for (const [campo, mensaje] of Object.entries(erroresDeCampo(error))) {
        const ruta = campo.replace(/^titular\./, 'ocupantes.0.');
        setError(ruta as Path<FormularioNuevaReserva>, { message: mensaje });
      }
      // 404, 409 y cualquier otro: mensaje general con significado de negocio
      setError('root.servidor', { message: mensajeDeError(error) });
    }
  }

  return (
    <form onSubmit={handleSubmit(alEnviar)} noValidate>
      <div className="campo-apartamento">
        <label htmlFor="identificacionApartamento">Apartamento</label>
        <select id="identificacionApartamento" {...register('identificacionApartamento')}>
          <option value="">Seleccione un apartamento</option>
          {apartamentos.map((apartamento) => (
            <option key={apartamento.identificacion} value={apartamento.identificacion}>
              {apartamento.nombre} ({apartamento.identificacion}) · hasta {apartamento.capacidad} personas
            </option>
          ))}
        </select>
        {codigoInicial && !codigoValido && <p>El apartamento solicitado no está disponible en el catálogo.</p>}
        {errors.identificacionApartamento && <p className="error">{errors.identificacionApartamento.message}</p>}
      </div>

      <div>
        <label htmlFor="fechaEntrada">Fecha de entrada</label>
        <input id="fechaEntrada" type="date" {...register('fechaEntrada')} />
        {errors.fechaEntrada && <p className="error">{errors.fechaEntrada.message}</p>}
      </div>

      <div>
        <label htmlFor="fechaSalida">Fecha de salida</label>
        <input id="fechaSalida" type="date" {...register('fechaSalida')} />
        {errors.fechaSalida && <p className="error">{errors.fechaSalida.message}</p>}
      </div>

      <div>
        <label htmlFor="horaEstimadaLlegada">Hora estimada de llegada (opcional)</label>
        <input id="horaEstimadaLlegada" type="time" {...register('horaEstimadaLlegada')} />
        {errors.horaEstimadaLlegada && <p className="error">{errors.horaEstimadaLlegada.message}</p>}
      </div>

      <fieldset>
        <legend>Ocupantes (el primero es el titular)</legend>

        {fields.map((campo, indice) => (
          <div key={campo.id} className="tarjeta">
            <strong>{indice === 0 ? 'Titular' : `Ocupante ${indice + 1}`}</strong>

            <label htmlFor={`documento-${indice}`}>Documento</label>
            <input id={`documento-${indice}`} {...register(`ocupantes.${indice}.documento`)} />
            {errors.ocupantes?.[indice]?.documento && (
              <p className="error">{errors.ocupantes[indice].documento.message}</p>
            )}

            <label htmlFor={`nombre-${indice}`}>Nombre</label>
            <input id={`nombre-${indice}`} {...register(`ocupantes.${indice}.nombre`)} />
            {errors.ocupantes?.[indice]?.nombre && (
              <p className="error">{errors.ocupantes[indice].nombre.message}</p>
            )}

            <label htmlFor={`fechaNacimiento-${indice}`}>Fecha de nacimiento</label>
            <input id={`fechaNacimiento-${indice}`} type="date" {...register(`ocupantes.${indice}.fechaNacimiento`)} />
            {errors.ocupantes?.[indice]?.fechaNacimiento && (
              <p className="error">{errors.ocupantes[indice].fechaNacimiento.message}</p>
            )}

            {indice > 0 && (
              <button type="button" onClick={() => remove(indice)}>Quitar</button>
            )}
          </div>
        ))}

        {superaCapacidad && <p className="error" role="alert">Retire ocupantes para respetar la capacidad del apartamento.</p>}
        <button type="button" onClick={() => append(OCUPANTE_VACIO)}
          disabled={isSubmitting || (apartamentoElegido !== undefined && fields.length >= apartamentoElegido.capacidad)}>
          Agregar ocupante
        </button>
      </fieldset>

      <button type="submit" disabled={isSubmitting || superaCapacidad}>
        {isSubmitting ? 'Creando…' : 'Crear reserva'}
      </button>

      {errors.root?.servidor && <p className="error" role="alert">{errors.root.servidor.message}</p>}
    </form>
);
}
