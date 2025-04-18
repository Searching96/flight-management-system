import React, { useEffect, useState } from 'react'
import { listAirports } from '../services/AirportService'
import { useNavigate } from 'react-router-dom';

const ListAirport = () => {
   const [airports, setAirports] = useState([]);

   const navigator = useNavigate();

   useEffect(() => {
      listAirports().then((response) => {
         setAirports(response.data);
      }).catch(error => {
         console.error("Error fetching airports:", error);
      })

   }, [])

   function addNewAirport() {
      navigator('/add-airport')
   }

   return (
      <div className="container">
         <h1 className='text-center'>List of Airports</h1>
         <button className='btn btn-primary mb-2' onClick={addNewAirport}>Add Airport</button>
         <table className='table table-striped table-bordered'>
            <thead>
               <tr>
                  <th>Airport ID</th>
                  <th>Airport Name</th>
               </tr>
            </thead>
            <tbody>
               {
                  airports.map(airport =>
                     <tr key={airport.id}>
                        <td>{airport.id}</td>
                        <td>{airport.name}</td>
                     </tr>)
               }
            </tbody>
         </table>
      </div>
   )
}

export default ListAirport