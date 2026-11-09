import { useState } from "react";
import { TarjetaApartamento } from "../components/TarjetaApartamento";
import { apartamentosEjemplo } from "../data/apartamentosEjemplo";

export default function Inicio(){
    const [personas, setPersonas] = useState(2);

    const queCaben = apartamentosEjemplo.filter((a) => a.capacidad >= personas);


    return(
        <section>
            <h1>Apartamentos</h1>

            <label>
                Personas:{' '}
                <input 
                    type="number" 
                    min={1}
                    value={personas}
                    onChange={(e) => setPersonas(Number(e.target.value))}
                />
            </label>

            <p>{queCaben.length} apatamento(s) adminten {personas} persona(s)</p>

            {queCaben.map((apartamento) => (
                <TarjetaApartamento 
                    key={apartamento.identificacion}
                    apartamento={apartamento}
                />
            ))}            
        </section>
    )
}