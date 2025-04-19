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

   function updateAirport(id) {
      navigator(`/edit-airport/${id}`)
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
                  <th>Actions</th>
               </tr>
            </thead>
            <tbody>
               {
                  airports.map(airport =>
                     <tr key={airport.id}>
                        <td>{airport.id}</td>
                        <td>{airport.name}</td>
                        <td>
                           <button className='btn btn-info' onClick={() => updateAirport(airport.id)}>Update</button>
                        </td>
                     </tr>)
               }
            </tbody>
         </table>
      </div>
   )
}

export default ListAirport