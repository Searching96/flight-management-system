import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { listAirports } from "../services/AirportService";
import { AirportDto } from "../models/Airport";

const ListAirport: React.FC = () => {
  const [airports, setAirports] = useState<AirportDto[]>([]);
  const navigate = useNavigate();

  useEffect(() => {
    listAirports()
      .then(setAirports)
      .catch((error) => {
        console.error("Error fetching airports:", error);
      });
  }, []);

  function addNewAirport() {
    navigate("/add-airport");
  }

  function editAirport(id: number) {
    navigate(`/edit-airport/${id}`);
  }

  // Optional: implement deleteAirport

  return (
    <div className="container">
      <h2>Airports</h2>
      <button className="btn btn-primary" onClick={addNewAirport}>
        Add Airport
      </button>
      <table className="table table-bordered">
        <thead>
          <tr>
            <th>Airport ID</th>
            <th>Name</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {airports.map((airport) => (
            <tr key={airport.id}>
              <td>{airport.id}</td>
              <td>{airport.name}</td>
              <td>
                <button
                  className="btn btn-info"
                  onClick={() => editAirport(airport.id)}
                >
                  Edit
                </button>
                {/* <button className="btn btn-danger" onClick={() => deleteAirport(airport.id)}>Delete</button> */}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default ListAirport;