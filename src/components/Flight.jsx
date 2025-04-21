import React, { useState, useEffect } from 'react'
import { listAirports } from '../services/AirportService'

const Flight = () => {
   const [airports, setAirports] = useState([]);
   const [departureAirport, setDepartureAirport] = useState('');
   const [arrivalAirport, setArrivalAirport] = useState('');
   const [flightDate, setFlightDate] = useState('');
   const [flightTime, setFlightTime] = useState('');
   const [flightDuration, setFlightDuration] = useState('');
   const [errors, setErrors] = useState({ flightDuration: '', airports: '' });

   useEffect(() => {
      // Fetch airports data
      listAirports().then((response) => {
         setAirports(response.data);
      }).catch((error) => {
         console.error('Error fetching airports: ', error);
      })
   }, []);

   const handleFlightDurationChange = (e) => {
      const value = e.target.value;

      // Validate that the input is a number
      if (!/^\d*$/.test(value)) {
         setErrors({ ...errors, flightDuration: 'Flight duration must be a number' });
      } else {
         setErrors({ ...errors, flightDuration: '' });
         setFlightDuration(value);
      }
   };

   const handleSubmit = (e) => {
      e.preventDefault();

      // Validate form before submission
      if (!flightDuration || isNaN(flightDuration)) {
         setErrors({ ...errors, flightDuration: 'Flight duration must be a valid number' });
         return;
      }

      if (departureAirport === arrivalAirport) {
         setErrors({ ...errors, airports: 'Departure and arrival airports cannot be the same' });
         return;
      }

      const flight = {
         departureAirport,
         arrivalAirport,
         flightDate,
         flightTime,
         flightDuration,
      };

      console.log('Flight: ', flight);
   };

   return (
      <div className="container">
         <h2 className="text-center">Create Flight</h2>
         <form>
            <div className="form-group mb-3">
               <label htmlFor="departureAirport">Departure Airport:</label>
               <select
                  id="departureAirport"
                  className={`form-control ${errors.airports ? 'is-invalid' : ''}`}
                  value={departureAirport}
                  onChange={(e) => setDepartureAirport(e.target.value)}
               >
                  <option value="">Select Departure Airport</option>
                  {airports.map((airport) => (
                     <option key={airport.id} value={airport.id}>
                        {airport.name}
                     </option>
                  ))}
               </select>
            </div>

            <div className="form-group mb-3">
               <label htmlFor="arrivalAirport">Arrival Airport:</label>
               <select
                  id="arrivalAirport"
                  className={`form-control ${errors.airports ? 'is-invalid' : ''}`}
                  value={arrivalAirport}
                  onChange={(e) => setArrivalAirport(e.target.value)}
               >
                  <option value="">Select Arrival Airport</option>
                  {airports.map((airport) => (
                     <option key={airport.id} value={airport.id}>
                        {airport.name}
                     </option>
                  ))}
               </select>
               {errors.airports && <div className="invalid-feedback">{errors.airports}</div>}
            </div>

            <div className="form-group mb-3">
               <label htmlFor="flightDate">Flight Date:</label>
               <input
                  type="date"
                  id="flightDate"
                  className="form-control"
                  value={flightDate}
                  onChange={(e) => setFlightDate(e.target.value)}
               />
            </div>

            <div className="form-group mb-3">
               <label htmlFor="flightTime">Flight Time:</label>
               <input
                  type="time"
                  id="flightTime"
                  className="form-control"
                  value={flightTime}
                  onChange={(e) => setFlightTime(e.target.value)}
               />
            </div>

            <div className="form-group mb-3">
               <label htmlFor="flightDuration">Flight Duration (in minutes):</label>
               <input
                  type="text"
                  id="flightDuration"
                  className={`form-control ${errors.flightDuration ? 'is-invalid' : ''}`}
                  value={flightDuration}
                  onChange={handleFlightDurationChange}
               />
               {errors.flightDuration && (
                  <div className="invalid-feedback">{errors.flightDuration}</div>
               )}
            </div>

            <button type="submit" className="btn btn-primary" onClick={handleSubmit}>
               Submit
            </button>
         </form>
      </div>
   )
}

export default Flight