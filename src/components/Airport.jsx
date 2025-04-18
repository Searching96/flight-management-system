import React, { useState } from 'react'
import { addAirport } from '../services/AirportService';
import { useNavigate } from 'react-router-dom';

const Airport = () => {

   const [airportName, setAirportName] = useState('');

   const [errors, setErrors] = useState({

   })

   const navigator = useNavigate();

   function handleAirportName(e) {
      setAirportName(e.target.value);
   }

   function saveAirport(e) {
      e.preventDefault();

      if (validateForm()) {
         const airport = { name: airportName };

         addAirport(airport).then((response) => {
            console.log(response.data);
            navigator('/airports');
         })
      }
   }

   // Validation code
   function validateForm() {
      let valid = true;

      const errorsCopy = { ...errors };

      if (airportName.trim()) {
         errorsCopy.airportName = '';
      } else {
         errorsCopy.airportName = 'Airport name is required';
         valid = false;
      }

      setErrors(errorsCopy);

      return valid;
   }

   return (
      <div className='container'>
         <br /> <br />
         <div className='row'>
            <div className='card col-md-6 offset-md-3 offset-md-3'>
               <h2 className='text-center'>Add Airport</h2>
               <div className='card-body'>
                  <form>
                     <div className='form-group mb-2'>
                        <label className='form-label'>Airport Name:</label>
                        <input
                           type='text'
                           placeholder='Enter airport name'
                           name='name'
                           className={`form-control ${errors.airportName ? 'is-invalid' : ''}`}
                           value={airportName}
                           onChange={handleAirportName}
                        ></input>
                        {errors.airportName && <div className='invalid-feedback'> {errors.airportName} </div>}
                     </div>

                     <button className='btn btn-success' onClick={saveAirport}>Submit</button>
                  </form>
               </div>
            </div>
         </div>
      </div>
   )
}

export default Airport