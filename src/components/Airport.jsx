import React, { useState, useEffect } from 'react'
import { addAirport, getAirport } from '../services/AirportService';
import { useNavigate, useParams } from 'react-router-dom';

const Airport = () => {

   const [airportName, setAirportName] = useState('');


   const { id } = useParams()
   const [errors, setErrors] = useState({
      airportName: ''
   })

   const navigator = useNavigate();

   // Populate airport data with the id into the update form
   useEffect(() => {
      if (id) {
         getAirport(id).then((response) => {
            setAirportName(response.data.name); // the var name is the same as API structure (Spring) (name not airportName)
         }).catch(error => {
            console.error(error);
         })
      }
   }, [id])

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

   function pageTitle() {
      if (id) {
         return <h2 className='text-center'>Update Airport</h2>
      } else {
         <h2 className='text-center'>Add Airport</h2>
      }
   }

   return (
      <div className='container'>
         <br /> <br />
         <div className='row'>
            <div className='card col-md-6 offset-md-3 offset-md-3'>
               {
                  pageTitle()
               }
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