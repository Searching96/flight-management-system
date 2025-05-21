import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { listFlights } from "../services/FlightService";
import { listAirports } from "../services/AirportService";
import { FlightDto } from "../models/Flight";
import { AirportDto } from "../models/Airport";
import ListAirport from "./ListAirport";

const ListFlight: React.FC = () => {
  const [flights, setFlights] = useState<FlightDto[]>([]);
  const [airports, setAirports] = useState<AirportDto[]>([]);
  const [airportMap, setAirportMap] = useState<Record<number, string>>({});
  const navigate = useNavigate();

useEffect(() => {
    listFlights().then(setFlights);
    listAirports().then((airportList) => {
      setAirports(airportList);
      // Build a mapping from id to name for quick lookup
      const map: Record<number, string> = {};
      airportList.forEach(a => { map[a.id] = a.name; });
      setAirportMap(map);
    });
  }, []);

  function addNewFlight() {
    navigate("/insert-flights");
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
          <th>Flight Number</th>
          <th>Departure Airport</th>
          <th>Arrival Airport</th>
          <th>Flight Date</th>
          <th>Flight Time</th>
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
          </tr>
        ))}
      </tbody>
    </table>
    </div>
  );
};

export default ListFlight;