import axios from "axios";

const REST_API_BASE_URL = 'http://localhost:8080/api/flights';

const REST_API_BASE_URL_FLIGHT_DETAILS = 'http://localhost:8080/api/flight-details';

export const getListFlights = () => axios.get(REST_API_BASE_URL);

export const addFlight = (airport) => axios.post(REST_API_BASE_URL, airport);

export const addFlightDetails = (flightDetails) => axios.post(REST_API_BASE_URL_FLIGHT_DETAILS, flightDetails);

export const getFlightDetails = (flightId, mediumAirportId) => axios.get(`${REST_API_BASE_URL_FLIGHT_DETAILS}/${flightId}/${mediumAirportId}`);

