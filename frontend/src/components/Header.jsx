import React from 'react'
import { NavLink } from 'react-router-dom'

const Header = () => {
   return (
      <div>
         <header className='header'>
            <nav className='navbar navbar-dark bg-dark d-flex justify-content-start w-100 px-4'>
               <NavLink className='navbar-brand' href='https://github.com/OctGuy'>Demo Java Sprint01</NavLink>
               <ul className='navbar-nav d-flex flex-row my-0'>
                  <li className='nav-item mx-2'>
                     <NavLink className='nav-link' to='/'>Dashboard</NavLink>
                  </li>
                  <li className='nav-item mx-2'>
                     <NavLink className='nav-link' to='/airports'>Airport List</NavLink>
                  </li>
                  <li className='nav-item mx-2'>
                     <NavLink className='nav-link' to='/flights'>Flight List</NavLink>
                  </li>
                  <li className='nav-item mx-2'>
                     <NavLink className='nav-link' to='/seat-classes'>Seat Class List</NavLink>
                  </li>
                                    <li className='nav-item mx-2'>
                     <NavLink className='nav-link' to='/insert-flights'>Flight Add</NavLink>
                  </li>
               </ul>
            </nav>
         </header>
      </div>
   )
}

export default Header