import React, { useState, useEffect } from 'react'
import { listAirports } from '../services/AirportService'
import { addFlight, addFlightDetails } from '../services/FlightService';
import { useNavigate } from 'react-router-dom';

const Flight = () => {
   const [airports, setAirports] = useState([]);
   const [departureAirportId, setDepartureAirport] = useState('');
   const [arrivalAirportId, setArrivalAirport] = useState('');
   const [flightDate, setFlightDate] = useState('');
   const [flightTime, setFlightTime] = useState('');
   const [flightDuration, setFlightDuration] = useState('');

   //const [errors, setErrors] = useState({ flightDuration: '', airports: '' });

   const navigator = useNavigate();

   const [mediumAirports, setMediumAirports] = useState([
      { airportId: '', stopTime: '', note: '' },
      { airportId: '', stopTime: '', note: '' },
   ]);

   const [errors, setErrors] = useState({});

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

   const handleMediumAirportChange = (index, field, value) => {
      const updatedMediumAirports = [...mediumAirports];
      updatedMediumAirports[index][field] = value;

      // Validate stop time to ensure it's a number
      if (field === 'stopTime' && field !== '' && !/^\d*$/.test(value)) {
         setErrors({ ...errors, [`stopTime${index}`]: 'Stop time must be a number' });
      } else {
         setErrors({ ...errors, [`stopTime${index}`]: '' });
      }

      setMediumAirports(updatedMediumAirports);
   };

   const handleSubmit = async (e) => {
      e.preventDefault();

      // Validate form before submission
      if (!flightDuration || isNaN(flightDuration)) {
         setErrors({ ...errors, flightDuration: 'Flight duration must be a valid number' });
         return;
      }

      if (departureAirportId === arrivalAirportId) {
         setErrors({ ...errors, airports: 'Departure and arrival airports cannot be the same' });
         return;
      }

      const mediumAirportIds = mediumAirports.map((mediumAirport) => mediumAirport.airportId);
      const allAirports = [departureAirportId, arrivalAirportId, ...mediumAirportIds];

      const uniqueAirports = new Set(allAirports.filter((id) => id !== ''));
      if (uniqueAirports.size !== allAirports.length) {
         setErrors({ ...errors, airports: 'All selected airports must be distinct' });
         return;
      }

      if (!mediumAirports[0].stopTime || !mediumAirports[1].stopTime
         || isNaN(mediumAirports[0].stopTime) || isNaN(mediumAirports[1].stopTime)
      ) {
         return;
      }

      const flight = {
         departureAirportId: departureAirportId,
         arrivalAirportId: arrivalAirportId,
         flightDate: flightDate,
         flightTime: flightTime,
         duration: parseInt(flightDuration),
      };

      try {
         // Submit the flight and get the flightId from the response
         const flightResponse = await addFlight(flight);
         const flightId = flightResponse.data.id;

         console.log('Flight created with ID: ', flightId);

         // Create flightDetails for each medium airport
         mediumAirports.forEach(async (mediumAirport) => {
            try {
               const flightDetail = {
                  flightId: flightId,
                  mediumAirportId: parseInt(mediumAirport.airportId),
                  stopTime: parseInt(mediumAirport.stopTime),
                  note: mediumAirport.note
               };

               console.log('FlightDetail: ', flightDetail);

               await addFlightDetails(flightDetail);

               console.log('Flight detail added successfully');
            } catch (detailError) {
               console.error('Error creating flight detail: ', detailError);
            }
         });

         navigator('/flights');
      } catch (flightError) {
         console.error('Error creating flight: ', flightError);
      }

      //console.log('Flight: ', flight);

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
                  value={departureAirportId}
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
                  value={arrivalAirportId}
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


            {/* Medium Airports Table */}
            <h3 className="text-center">List of Medium Airports</h3>
            <table className="table table-bordered text-center">
               <thead>
                  <tr>
                     <th>Medium Flight</th>
                     <th>Stop Time</th>
                     <th>Note</th>
                  </tr>
               </thead>
               <tbody>
                  {mediumAirports.map((mediumAirport, index) => (
                     <tr key={index}>
                        <td>
                           <select
                              className="form-control"
                              value={mediumAirport.airportId}
                              onChange={(e) =>
                                 handleMediumAirportChange(index, 'airportId', e.target.value)
                              }
                           >
                              <option value="">Select Medium Airport</option>
                              {airports.map((airport) => (
                                 <option key={airport.id} value={airport.id}>
                                    {airport.name}
                                 </option>
                              ))}
                           </select>
                        </td>
                        <td>
                           <input
                              type="text"
                              className={`form-control ${errors[`stopTime${index}`] ? 'is-invalid' : ''
                                 }`}
                              value={mediumAirport.stopTime}
                              onChange={(e) =>
                                 handleMediumAirportChange(index, 'stopTime', e.target.value)
                              }

                           />
                           {errors[`stopTime${index}`] && (
                              <div className="invalid-feedback">{errors[`stopTime${index}`]}</div>
                           )}
                        </td>
                        <td>
                           <input
                              type="text"
                              className="form-control"
                              value={mediumAirport.note}
                              onChange={(e) =>
                                 handleMediumAirportChange(index, 'note', e.target.value)
                              }
                           />
                        </td>
                     </tr>
                  ))}
               </tbody>
            </table>


            <button type="submit" className="btn btn-primary" onClick={handleSubmit}>
               Submit
            </button>
         </form>
      </div>
   )
}

export default Flight