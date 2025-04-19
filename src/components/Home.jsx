import React from 'react'
import { useNavigate } from 'react-router-dom'

const Home = () => {
   const navigator = useNavigate();

   function viewListAirports() {
      navigator('/airports');
   }

   return (
      <div className='container'>
         <button className='btn btn-info' onClick={viewListAirports}>View List Airports</button>
      </div>
   )
}

export default Home