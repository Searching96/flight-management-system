import './App.css'
import Airport from './components/Airport'
import Footer from './components/Footer'
import Header from './components/Header'
import ListAirport from './components/ListAirport'
import { BrowserRouter, Route, Routes } from 'react-router-dom'

function App() {
  return (
    <>
      <BrowserRouter>
        <Header />
        <Routes>
          {/* // http://localhost:3000 */}
          <Route path='/' element={<ListAirport />}></Route>
          {/* // http://localhost:3000/airports */}
          <Route path='/airports' element={<ListAirport />}></Route>
          {/* // http://localhost:3000/add-airport */}
          <Route path='/add-airport' element={<Airport />}></Route>
        </Routes>
        <Footer />
      </BrowserRouter>
    </>
  )
}

export default App
