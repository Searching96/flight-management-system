import React, { useState, useEffect } from 'react';
import {
   getMaxMediumAirport,
   getMinFlightDuration,
   getMaxFlightDuration,
   getMaxStopDuration,
   updateMaxMediumAirport,
   updateMinFlightDuration,
   updateMaxFlightDuration,
   updateMaxStopDuration,
} from '../services/ParameterService';

const Parameters = () => {
   const [parameters, setParameters] = useState({
      maxMediumAirport: '',
      minFlightDuration: '',
      maxFlightDuration: '',
      maxStopDuration: '',
   });

   const [editable, setEditable] = useState({
      maxMediumAirport: false,
      minFlightDuration: false,
      maxFlightDuration: false,
      maxStopDuration: false,
   });

   const [errors, setErrors] = useState({});

   useEffect(() => {
      fetchParameters();
   }, []);

   const fetchParameters = async () => {
      try {
         const maxMediumAirport = await getMaxMediumAirport();
         const minFlightDuration = await getMinFlightDuration();
         const maxFlightDuration = await getMaxFlightDuration();
         const maxStopDuration = await getMaxStopDuration();

         setParameters({
            maxMediumAirport: maxMediumAirport.data,
            minFlightDuration: minFlightDuration.data,
            maxFlightDuration: maxFlightDuration.data,
            maxStopDuration: maxStopDuration.data,
         });
      } catch (error) {
         console.error('Error fetching parameters:', error);
      }
   };

   const handleEdit = (key) => {
      setEditable({ ...editable, [key]: true });
   };

   const handleChange = (key, value) => {
      setParameters({ ...parameters, [key]: value });
   };

   const handleSubmit = async (key) => {
      try {
         if (!parameters[key]) {
            setErrors({ ...errors, [key]: 'Value cannot be empty' });
            return;
         }

         // Validate if the value is a valid integer
         if (
            ['maxMediumAirport', 'minFlightDuration', 'maxFlightDuration', 'maxStopDuration'].includes(key) &&
            (isNaN(parameters[key]) || !Number.isInteger(Number(parameters[key])))
         ) {
            setErrors({ ...errors, [key]: 'Value must be an integer' });
            return;
         }

         setErrors({ ...errors, [key]: '' });

         console.log(`Updating ${key} with value:`, parameters[key]);

         // Parse the value to an integer before sending it to the API
         const parsedValue = parseInt(parameters[key], 10);

         switch (key) {
            case 'maxMediumAirport':
               await updateMaxMediumAirport(parsedValue);
               break;
            case 'minFlightDuration':
               await updateMinFlightDuration(parsedValue);
               break;
            case 'maxFlightDuration':
               await updateMaxFlightDuration(parsedValue);
               break;
            case 'maxStopDuration':
               await updateMaxStopDuration(parsedValue);
               break;
            default:
               break;
         }

         setEditable({ ...editable, [key]: false });
      } catch (error) {
         console.error(`Error updating ${key}:`, error.response?.data || error.message);
      }
   };

   return (
      <div className="container">
         <h1 className="text-center">Parameters</h1>
         <table className="table table-striped table-bordered">
            <thead>
               <tr>
                  <th>Parameter</th>
                  <th>Value</th>
                  <th>Action</th>
               </tr>
            </thead>
            <tbody>
               {Object.keys(parameters).map((key) => (
                  <tr key={key}>
                     <td>{key.replace(/([A-Z])/g, ' $1').trim()}</td>
                     <td>
                        <input
                           type="text"
                           className={`form-control ${errors[key] ? 'is-invalid' : ''}`}
                           value={parameters[key]}
                           onChange={(e) => handleChange(key, e.target.value)}
                           disabled={!editable[key]}
                        />
                        {errors[key] && <div className="invalid-feedback">{errors[key]}</div>}
                     </td>
                     <td>
                        {!editable[key] ? (
                           <button className="btn btn-info" onClick={() => handleEdit(key)}>
                              Update
                           </button>
                        ) : (
                           <button className="btn btn-success" onClick={() => handleSubmit(key)}>
                              Submit
                           </button>
                        )}
                     </td>
                  </tr>
               ))}
            </tbody>
         </table>
      </div>
   );
};

export default Parameters;