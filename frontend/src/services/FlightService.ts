import axios from "axios";
import { FlightDto } from "../models/Flight";
import { DOMAIN_URL_DEFAULT, API_URL } from "./config";

const BASE_URL = `${DOMAIN_URL_DEFAULT}${API_URL.FLIGHTS}`;
const BASE_URL_FLIGHT_DETAILS = `${DOMAIN_URL_DEFAULT}${API_URL.FLIGHT_DETAILS}`;

export const listFlights = async (): Promise<FlightDto[]> => {
  const response = await axios.get<FlightDto[]>(BASE_URL);
  return response.data;
};

export const addFlight = async (
  flight: Omit<FlightDto, "id">
): Promise<FlightDto> => {
  const response = await axios.post<FlightDto>(BASE_URL, flight);
  return response.data;
};

export const getFlight = async (id: number): Promise<FlightDto> => {
  const response = await axios.get<FlightDto>(`${BASE_URL}/${id}`);
  return response.data;
};

export const updateFlight = async (
  id: number,
  flight: Omit<FlightDto, "id">
): Promise<FlightDto> => {
  const response = await axios.put<FlightDto>(`${BASE_URL}/${id}`, flight);
  return response.data;
};

// Add types for flightDetails as needed
export const addFlightDetails = async (flightDetails: any) =>
  axios.post(BASE_URL_FLIGHT_DETAILS, flightDetails);

export const getFlightDetails = async (flightId: number, mediumAirportId: number) =>
  axios.get(`${BASE_URL_FLIGHT_DETAILS}/${flightId}/${mediumAirportId}`);