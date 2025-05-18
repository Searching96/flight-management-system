import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getListFlights } from '../services/FlightService';
import { getAirport } from '../services/AirportService';

const ListFlight = () => {
   const [flights, setFlights] = useState([]);
   const navigator = useNavigate();

   useEffect(() => {
      getAllFlights();
   }, []);

   function getAllFlights() {
      getListFlights()
         .then((response) => {
            setFlights(response.data);
         })
         .catch((error) => {
            console.error('Error fetching flights:', error);
         });
   }

   function addNewFlight() {
      navigator('/add-flight');
   }

   // function updateFlight(id) {
   //    //navigator(`/edit-flight/${id}`);
   //    console.log('Update flight');
   // }

   // function removeFlight(id) {
   //    // deleteFlight(id)
   //    //    .then((response) => {
   //    //       console.log(response.data);
   //    //       getAllFlights(); // Refresh the list after deletion
   //    //    })
   //    //    .catch((error) => {
   //    //       console.error('Error deleting flight:', error);
   //    //    });
   //    console.log('Delete flight')
   // }

   return (
      <div className="container">
         <h1 className="text-center">List of Flights</h1>
         <button className="btn btn-primary mb-2" onClick={addNewFlight}>
            Add Flight
         </button>
         <table className="table table-striped table-bordered">
            <thead>
               <tr>
                  <th>Flight ID</th>
                  <th>Departure Airport</th>
                  <th>Arrival Airport</th>
                  <th>Flight Date</th>
                  <th>Flight Time</th>
                  <th>Flight Duration</th>
                  <th>Actions</th>
               </tr>
            </thead>
            <tbody>
               {flights.map((flight) => (
                  <tr key={flight.id}>
                     <td>{flight.id}</td>
                     <td>
                        {
                           getAirport(flight.departureAirportId).name
                        }
                     </td>
                     <td>{getAirport(flight.arrivalAirportId).name}</td>
                     <td>{flight.flightDate}</td>
                     <td>{flight.flightTime}</td>
                     <td>{flight.duration}</td>
                     <td>
                        {/* <button
                           className="btn btn-info"
                           onClick={() => updateFlight(flight.id)}
                        >
                           Update
                        </button> */}
                        {/* <button
                           className="btn btn-danger"
                           onClick={() => removeFlight(flight.id)}
                        >
                           Delete
                        </button> */}
                     </td>
                  </tr>
               ))}
            </tbody>
         </table>
      </div>
   );
};

export default ListFlight;