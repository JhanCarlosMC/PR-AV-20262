//Componente sin Props
function Header() {
  const nombreNegocio: String = "La Marteja";
  const ubicacion: String = "Salento Quindio";
  const abierto: boolean = true;
  const cantHabitaciones: Number = 10;

    return (
        <header>
            <h1>Hola mi Alojamiento se llama {nombreNegocio}</h1>
            <p>Estamos ubicados en {ubicacion} </p>
            <p>{abierto ? 'Estamos Abiertos' : 'Volvemos Pronto...'}</p>
            <p>Contamos con variedad de habitaciones, y una cantidad de {Number(cantHabitaciones)} </p>
        </header>
    )
}

export default Header