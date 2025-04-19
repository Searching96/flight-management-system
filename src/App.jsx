import './App.css'
import Airport from './components/Airport'
import Footer from './components/Footer'
import Header from './components/Header'
import Home from './components/Home'
import ListAirport from './components/ListAirport'
import { BrowserRouter, Route, Routes } from 'react-router-dom'

function App() {
  return (
    <>
      <BrowserRouter>
        <Header />
        <Routes>
          {/* // http://localhost:3000 */}
          <Route path='/' element={<Home />}></Route>
          {/* // http://localhost:3000/airports */}
          <Route path='/airports' element={<ListAirport />}></Route>
          {/* // http://localhost:3000/add-airport */}
          <Route path='/add-airport' element={<Airport />}></Route>
          {/* // http://localhost:3000/edit-airport/1 */}
          <Route path='/edit-airport/:id' element={<Airport />}></Route>
        </Routes>
        <Footer />
      </BrowserRouter>
    </>
  )
}

export default App
