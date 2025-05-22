import './App.css'
import Airport from './components/Airport'
import Footer from './components/Footer'
import Header from './components/Header'
import Home from './components/Home'
import ListAirport from './components/ListAirport'
import { BrowserRouter, Route, Routes } from 'react-router-dom'
import ListSeatClass from './components/ListSeatClass'
import SeatClass from './components/SeatClass'
import Flight from './components/Flight'
import ListFlight from './components/ListFlight'
import ParameterForm from './components/Parameter'
import FlightForm from './components/FlightForm'

function App() {
  return (
    <>
      <BrowserRouter>
        <Header />
        <main className="container py-4">
          <Routes>
            {/* // http://localhost:3000 */}
            <Route path='/' element={<Home />}></Route>

            {/* // http://localhost:3000/airports */}
            <Route path='/airports' element={<ListAirport />}></Route>

            {/* // http://localhost:3000/add-airport */}
            <Route path='/add-airport' element={<Airport />}></Route>

            {/* // http://localhost:3000/edit-airport/1 */}
            <Route path='/edit-airport/:id' element={<Airport />}></Route>

            {/* // http://localhost:3000/seat-classes */}
            <Route path='/seat-classes' element={<ListSeatClass />}></Route>

            {/* // http://localhost:3000/add-seat-class */}
            <Route path='/add-seat-class' element={<SeatClass />}></Route>

            {/* // http://localhost:3000/edit-seat-class/id */}
            <Route path='/edit-seat-class/:id' element={<SeatClass />}></Route>

            {/* // http://localhost:3000/flights */}
            <Route path='/flights' element={<ListFlight />}></Route>

            {/* // http://localhost:3000/add-flight */}
            <Route path='/add-flight' element={<FlightForm />}></Route>

            {/* // http://localhost:3000/edit-flight/id */}
            <Route path="/edit-flight/:id" element={<FlightForm />} />

            {/* // http://localhost:3000/parameter */}
            <Route path='/parameter' element={<ParameterForm />}></Route>

          </Routes>
        </main>
        <Footer />
      </BrowserRouter>
    </>
  )
}

export default App
