import React, { useState, useEffect } from 'react';
import { listAirports } from '../services/AirportService';
import { addFlight, addFlightDetails } from '../services/FlightService';
import {
   getMaxMediumAirport,
   getMinFlightDuration,
   getMaxFlightDuration,
   getMaxStopDuration,
} from '../services/ParameterService'; // Import the services
import { useNavigate } from 'react-router-dom';

const Flight = () => {
   const [airports, setAirports] = useState([]);
   const [departureAirportId, setDepartureAirport] = useState('');
   const [arrivalAirportId, setArrivalAirport] = useState('');
   const [flightDate, setFlightDate] = useState('');
   const [flightTime, setFlightTime] = useState('');
   const [flightDuration, setFlightDuration] = useState('');
   const [mediumAirports, setMediumAirports] = useState([]);
   const [errors, setErrors] = useState({});
   const [minFlightDuration, setMinFlightDuration] = useState(0);
   const [maxFlightDuration, setMaxFlightDuration] = useState(0);
   const [maxStopDuration, setMaxStopDuration] = useState(0);
   const navigator = useNavigate();

   useEffect(() => {
      // Fetch airports data
      listAirports()
         .then((response) => {
            setAirports(response.data);
         })
         .catch((error) => {
            console.error('Error fetching airports: ', error);
         });

      // Fetch parameters for validation
      getMaxMediumAirport()
         .then((response) => {
            const maxMediumAirport = response.data; // Assuming the API returns the number directly
            const initialMediumAirports = Array.from({ length: maxMediumAirport }, () => ({
               airportId: '',
               stopTime: '',
               note: '',
            }));
            setMediumAirports(initialMediumAirports);
         })
         .catch((error) => {
            console.error('Error fetching maxMediumAirport: ', error);
         });

      getMinFlightDuration()
         .then((response) => setMinFlightDuration(response.data))
         .catch((error) => console.error('Error fetching minFlightDuration: ', error));

      getMaxFlightDuration()
         .then((response) => setMaxFlightDuration(response.data))
         .catch((error) => console.error('Error fetching maxFlightDuration: ', error));

      getMaxStopDuration()
         .then((response) => setMaxStopDuration(response.data))
         .catch((error) => console.error('Error fetching maxStopDuration: ', error));
   }, []);

   const handleFlightDurationChange = (value) => {

      // Validate that the input is a number and within the allowed range
      if (value !== '' && !/^\d*$/.test(value)) {
         setErrors({ ...errors, flightDuration: 'Flight duration must be a number' });
      } else if (value > maxFlightDuration) {
         setErrors({
            ...errors,
            flightDuration: `Flight duration must not exceed ${maxFlightDuration} minutes`,
         });
      } else if (value < minFlightDuration) {
         setErrors({
            ...errors,
            flightDuration: `Flight duration must be at least ${minFlightDuration} minutes`,
         });
      } else {
         setErrors({ ...errors, flightDuration: '' });
      }

      setFlightDuration(value);
   };

   const handleMediumAirportChange = (index, field, value) => {
      const updatedMediumAirports = [...mediumAirports];
      updatedMediumAirports[index][field] = value;

      // Validate stop time to ensure it's a number and within maxStopDuration
      if (field === 'stopTime' && field !== '' && !/^\d*$/.test(value)) {
         setErrors({ ...errors, [`stopTime${index}`]: 'Stop time must be a number' });
      } else if (field === 'stopTime' && value > maxStopDuration) {
         setErrors({
            ...errors,
            [`stopTime${index}`]: `Stop time must not exceed ${maxStopDuration} minutes`,
         });
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

      if (
         mediumAirports.some(
            (mediumAirport) =>
               !mediumAirport.stopTime || isNaN(mediumAirport.stopTime) || mediumAirport.stopTime > maxStopDuration
         )
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
                  note: mediumAirport.note,
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
                  onChange={(e) =>
                     handleFlightDurationChange(e.target.value)
                  }
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
                              <div className="invalid-feedback">
                                 {errors[`stopTime${index}`]}
                              </div>
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
   );
};

export default Flight;