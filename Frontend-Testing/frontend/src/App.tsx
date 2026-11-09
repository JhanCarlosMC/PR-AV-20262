import { Route, Routes } from "react-router";
import { Encabezado } from "./components/Encabezado";
import Inicio from "./pages/Inicio";
import Registro from "./pages/Registro";
import DetalleApartamento from "./pages/DetalleApartamento";
import NoEncontrada from "./pages/NoEncontrada";
import Login from "./pages/Login";

export default function App(){

    return(
        <>
            <Encabezado nombreAlojamiento = "ApartaHotel La Marteja" />
            
            <main>
                <Routes>
                    <Route path="/" element={<Inicio/>} />
                    <Route path="/login" element={<Login/>} />
                    <Route path="/registro" element={<Registro/>} />
                    <Route path="/apartamentos/:identificacion" element={<DetalleApartamento/>} />
                    <Route path="*" element={<NoEncontrada/>} />
                    
                </Routes>
            </main>
        </>
    )
}