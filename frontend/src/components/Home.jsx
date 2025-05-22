import React from 'react'
import { useNavigate } from 'react-router-dom'

const Home = () => {
   const navigator = useNavigate();

   function viewListAirports() {
      navigator('/airports');
   }

   function viewListSeatClasses() {
      navigator('/seat-classes');
   }

   function viewListFlights() {
      navigator('/flights');
   }

   function viewParameters() {
      navigator('/parameter');
   }

   return (
      <div className='container'>
         <button className='btn btn-info' onClick={viewListAirports}>View List Airports</button>
         <button className='btn btn-info' onClick={viewListSeatClasses}>View List Seat Classes</button>
         <button className='btn btn-info' onClick={viewListFlights}>View List Flights</button>
         <button className='btn btn-info' onClick={viewParameters}>View Parameters</button>
      </div>
   )
}

export default Home