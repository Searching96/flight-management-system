import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { listFlights } from "../services/FlightService";
import { FlightDto } from "../models/Flight";
import { listAirports } from "../services/AirportService";
import { AirportDto } from "../models/Airport";

const ListFlight: React.FC = () => {
  const [flights, setFlights] = useState<FlightDto[]>([]);
  const [airports, setAirports] = useState<AirportDto[]>([]);
  const navigate = useNavigate();

  // Create a lookup for airport names by ID for fast rendering
  const airportMap = airports.reduce<Record<number, string>>(
    (acc, airport) => {
      acc[airport.id] = airport.name;
      return acc;
    },
    {}
  );

  useEffect(() => {
    listFlights()
      .then(setFlights)
      .catch((error) => {
        console.error("Error fetching flights:", error);
      });
    listAirports()
      .then(setAirports)
      .catch((error) => {
        console.error("Error fetching airports:", error);
      });
  }, []);

  function addNewFlight() {
    navigate("/add-flight");
  }

  function editFlight(id: number) {
    navigate(`/edit-flight/${id}`);
  }

  // Optional: implement deleteFlight

  return (
    <div className="container">
      <h2>Flights</h2>
      <button className="btn btn-primary" onClick={addNewFlight}>
        Add Flight
      </button>
      <table className="table table-bordered">
        <thead>
          <tr>
            <th>Flight ID</th>
            <th>Flight Number</th>
            <th>Airline</th>
            <th>Departure Airport</th>
            <th>Arrival Airport</th>
            <th>Departure Time</th>
            <th>Arrival Time</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {flights.map((flight) => (
            <tr key={flight.id}>
              <td>{flight.id}</td>
              <td>{flight.flightNumber}</td>
              <td>{flight.airline}</td>
              <td>{airportMap[flight.departureAirportId] || flight.departureAirportId}</td>
              <td>{airportMap[flight.arrivalAirportId] || flight.arrivalAirportId}</td>
              <td>{flight.departureTime}</td>
              <td>{flight.arrivalTime}</td>
              <td>
                <button
                  className="btn btn-info"
                  onClick={() => editFlight(flight.id)}
                >
                  Edit
                </button>
                {/* <button className="btn btn-danger" onClick={() => deleteFlight(flight.id)}>Delete</button> */}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default ListFlight;