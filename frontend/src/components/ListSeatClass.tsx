import React, { useEffect, useState } from 'react'
import { listSeatClasses } from '../services/SeatClassService';
import { SeatClassDto } from '../models/SeatClass';
import { useNavigate } from 'react-router-dom'

const ListSeatClass = () => {
   const [seatClasses, setSeatClasses] = useState<SeatClassDto[]>([]);

   const navigator = useNavigate();

   useEffect(() => {
      getAllSeatClasses();
   }, [])

   function getAllSeatClasses() {
      listSeatClasses().then((response) => {
         setSeatClasses(response);
      }).catch(error => {
         console.error("Error fetching seat classes: ", error);
      })
   }

   function addNewSeatClass() {
      navigator('/add-seat-class')
   }

   function updateSeatClass(id) {
      navigator(`/edit-seat-class/${id}`)
   }

   // function removeSeatClass(id) {
   //    console.log(id);

   //    deleteSeatClass(id).then((response) => {
   //       console.log(response.data);
   //       getAllSeatClasses(); // new list after delete
   //    }).catch(error => {
   //       console.log(error);
   //    })
   // }

   return (
      <div className="container">
         <h1 className='text-center'>List of Seat Classes</h1>
         <button className='btn btn-primary mb-2' onClick={addNewSeatClass}>Add Seat Class</button>
         <table className='table table-striped table-bordered'>
            <thead>
               <tr>
                  <th>Seat Class ID</th>
                  <th>Seat Class Name</th>
                  <th>Actions</th>
               </tr>
            </thead>
            <tbody>
               {
                  seatClasses.map(seatClass =>
                     <tr key={seatClass.id}>
                        <td>{seatClass.id}</td>
                        <td>{seatClass.seatName}</td>
                        <td>
                           <button className='btn btn-info' onClick={() => updateSeatClass(seatClass.id)}>Update</button>
                           {/* <button className='btn btn-danger' onClick={() => removeSeatClass(seatClass.id)}>Delete</button> */}
                        </td>
                     </tr>)
               }
            </tbody>
         </table>
      </div>
   )
}

export default ListSeatClass