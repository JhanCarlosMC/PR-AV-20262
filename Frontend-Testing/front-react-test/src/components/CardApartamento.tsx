import { useState } from "react"

interface CardApartamentoProps {
    nombre: String
    descripcion: String
    precio: String
}

//Componente con Props
function CardApartamento({nombre, descripcion, precio}: CardApartamentoProps){
    const [cantReservas, setCantReservas] = useState<number>(0);

    function agregarReserva(){
        setCantReservas(actual => actual+1);
    }

    return(
        <div className="tarjeta">
            <h3>{nombre}</h3>
            <p>{descripcion}</p>
            <p>{precio}</p>

            <button type="button" onClick={agregarReserva}>
                AgregarReserva
            </button>

            <p>Total de reservas: {cantReservas}</p>
        </div>
    )
}

export default CardApartamento