import { Link, useParams } from 'react-router'
import { apartamentosEjemplo } from '../data/apartamentosEjemplo'

export default function DetalleApartamento(){
    // Ruta = /apartamentos/:identificacion
    const { identificacion } = useParams();
    const apartamento = apartamentosEjemplo.find((a) => a.identificacion === identificacion);

    if(!apartamento){
        return <p>El apartamento con identificacion {identificacion} no existe.</p>
    }

    return(
        <section>
            <h1>{apartamento.nombre}</h1>
            <p>Identificacion: {apartamento.identificacion}</p>
            <p>Dormitorios: {apartamento.dormitorios}</p>
            <p>Capacidad: {apartamento.capacidad}</p>


            <Link to="/">Volver</Link>
        </section>
    );
}