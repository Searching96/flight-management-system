import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { listFlights, deleteFlight } from "../services/FlightService";
import { listAirports } from "../services/AirportService";
import { FlightDto } from "../models/Flight";

const ListFlight: React.FC = () => {
  const [flights, setFlights] = useState<FlightDto[]>([]);
  const [airportMap, setAirportMap] = useState<Record<number, string>>({});
  const navigate = useNavigate();

  useEffect(() => {
    listFlights().then(setFlights);

    listAirports().then((airportList) => {
      // Build a mapping from id to name for quick lookup
      const map: Record<number, string> = {};
      airportList.forEach(a => { map[a.id] = a.name; });
      setAirportMap(map);
    });
  }, []);

  function addNewFlight() {
    navigate("/add-flight");
  }

  function editFlight(id: number) {
    navigate(`/edit-flight/${id}`);
  }

  function deleteFlightHandler(id: number) {
    if (!window.confirm('Are you sure you want to delete this flight?')) return;
    deleteFlight(id)
      .then(() => {
        setFlights(flights => flights.filter(flight => flight.id !== id));
      })
      .catch((err) => {
        alert('Failed to delete flight');
        console.error(err);
      });
      alert(`Deleted flight ${id} successfully.`);
  }

  return (
    <div className="container">
      <h2 className="mb-4">Flights</h2>

      <div className="mb-3">
        <button className="btn btn-primary" onClick={addNewFlight}>
          Add Flight
        </button>
      </div>

      <table className="table table-bordered align-middle">
        <thead className="table-light">
          <tr>
            <th>Flight Number</th>
            <th>Departure Airport</th>
            <th>Arrival Airport</th>
            <th>Flight Date</th>
            <th>Flight Time</th>
            <th>Duration</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {flights.map((flight) => (
            <tr key={flight.id}>
              <td>{flight.id}</td>
              <td>{airportMap[flight.departureAirportId] || flight.departureAirportId}</td>
              <td>{airportMap[flight.arrivalAirportId] || flight.arrivalAirportId}</td>
              <td>{flight.flightDate}</td>
              <td>{flight.flightTime}</td>
              <td>{flight.duration}</td>
              <td>
                <div className="d-flex gap-2">
                  <button className="btn btn-info" onClick={() => editFlight(flight.id)}>Update</button>
                  <button className='btn btn-danger' onClick={() => deleteFlightHandler(flight.id)}>Delete</button>
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default ListFlight;