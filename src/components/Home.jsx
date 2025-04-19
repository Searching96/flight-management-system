import React from 'react'
import { useNavigate } from 'react-router-dom'

const Home = () => {
   const navigator = useNavigate();

   function viewListAirports() {
      navigator('/airports');
   }

   function viewListSeatClasses() {
      navigator('/seat-classes')
   }

   return (
      <div className='container'>
         <button className='btn btn-info' onClick={viewListAirports}>View List Airports</button>
         <button className='btn btn-info' onClick={viewListSeatClasses}>View List Seat Classes</button>
      </div>
   )
}

export default Home