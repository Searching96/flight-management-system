import React, { useState, useEffect } from 'react'
import { addSeatClass, getSeatClass, updateSeatClass } from '../services/SeatClassService';
import { useNavigate, useParams } from 'react-router-dom';

const SeatClass = () => {
   const [seatName, setSeatName] = useState('');

   const { id } = useParams();
   const [errors, setErrors] = useState({
      seatName: ''
   })

   const navigator = useNavigate();

   // Populate seat class data with the id into the update form
   useEffect(() => {
      if (id) {
         getSeatClass(id).then((response) => {
            setSeatName(response.data.seatName); // the var name is the same as API structure (Spring)
         }).catch(error => {
            console.error(error);
         })
      }
   }, [id])

   function handleSeatName(e) {
      setSeatName(e.target.value);
   }

   function saveOrUpdateSeatClass(e) {
      e.preventDefault();
      console.log(id);

      if (validateForm()) {
         const seatClass = { seatName: seatName };
         console.log(seatClass);

         if (id) {
            updateSeatClass(id, seatClass).then((response) => {
               console.log(response.data);
               navigator('/seat-classes')
            }).catch(error => {
               console.error(error);
            })
         } else {
            addSeatClass(seatClass).then((response) => {
               console.log(response.data);
               navigator('/seat-classes');
            }).catch(error => {
               console.error(error);
            })
         }
      }
   }

   // validation code
   function validateForm() {
      let valid = true;

      const errorsCopy = { ...errors };

      if (seatName.trim()) {
         errorsCopy.seatName = '';
      } else {
         errorsCopy.seatName = 'Seat class name is required';
         valid = false;
      }

      setErrors(errorsCopy);

      return valid;
   }

   function pageTitle() {
      if (id) {
         return <h2 className='text-center'>Update Seat Class</h2>
      } else {
         return <h2 className='text-center'>Add Seat Class</h2>
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
                        <label className='form-label'>Seat Class Name:</label>
                        <input
                           type='text'
                           placeholder='Enter seat class name'
                           name='name'
                           className={`form-control ${errors.seatName ? 'is-invalid' : ''}`}
                           value={seatName}
                           onChange={handleSeatName}
                        ></input>
                        {errors.seatName && <div className='invalid-feedback'> {errors.seatName} </div>}
                     </div>

                     <button className='btn btn-success' onClick={saveOrUpdateSeatClass}>Submit</button>
                  </form>
               </div>
            </div>
         </div>
      </div>
   )
}

export default SeatClass