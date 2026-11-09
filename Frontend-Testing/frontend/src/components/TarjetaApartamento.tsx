import { Link } from 'react-router';
import type { ApartamentoResumen } from "../models/apartamento"

interface TarjetaApartamentoProps {
    apartamento: ApartamentoResumen;
}

export function TarjetaApartamento({ apartamento }: TarjetaApartamentoProps) {
    return (
        <article>
            <h3>{apartamento.nombre}</h3>
            <p>
                {apartamento.dormitorios} dormitorio(s) · hasta {apartamento.capacidad} personas
            </p>
            <Link to={`/apartamentos/${apartamento.identificacion}`}>
            Ver detalle
            </Link>
        </article>
    )
}