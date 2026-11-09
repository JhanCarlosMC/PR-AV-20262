import './App.css';
import Header from './components/Header'
import CardApartamento from './components/CardApartamento'

//Componente
function App() {

  //Return
  return (
    <>
      <Header />

      <main>
        <CardApartamento
          nombre= 'La Marteja 1'
          descripcion= 'Apartamento con vista al pueblo'
          precio= '70.000COP c/u'
        />

        <CardApartamento
          nombre= 'La Marteja 2'
          descripcion= 'Apartamento con Jaccuzi'
          precio= '100.000COP c/u'
        />

        <CardApartamento
          nombre= 'La Marteja 3'
          descripcion= 'Apartamento con todos los sevicios y 2 habitaciones'
          precio= '80.000COP c/u'
        />

      </main>
    </>
  )
}

export default App