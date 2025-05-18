import axios from "axios";

import { DOMAIN_URL_DEFAULT, API_URL } from "./config";

const BASE_URL_FLIGHTS = `${DOMAIN_URL_DEFAULT}${API_URL.FLIGHTS}`;

const BASE_URL_FLIGHT_DETAILS = `${DOMAIN_URL_DEFAULT}${API_URL.FLIGHT_DETAILS}`;

export const getListFlights = () => axios.get(BASE_URL_FLIGHTS);

export const addFlight = (airport) => axios.post(BASE_URL_FLIGHTS, airport);

export const addFlightDetails = (flightDetails) => axios.post(BASE_URL_FLIGHT_DETAILS, flightDetails);

export const getFlightDetails = (flightId, mediumAirportId) => axios.get(`${BASE_URL_FLIGHT_DETAILS}/${flightId}/${mediumAirportId}`);

