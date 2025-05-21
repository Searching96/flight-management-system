import axios from "axios";
import { AirportDto } from "../models/Airport";
import { DOMAIN_URL_DEFAULT, API_URL } from "./config";

const BASE_URL = `${DOMAIN_URL_DEFAULT}${API_URL.AIRPORTS}`;

export const listAirports = async (): Promise<AirportDto[]> => {
  const response = await axios.get<AirportDto[]>(BASE_URL);
  return response.data;
};

export const addAirport = async (
  airport: Omit<AirportDto, "id">
): Promise<AirportDto> => {
  const response = await axios.post<AirportDto>(BASE_URL, airport);
  return response.data;
};

export const getAirport = async (id: number): Promise<AirportDto> => {
  const response = await axios.get<AirportDto>(`${BASE_URL}/${id}`);
  return response.data;
};

export const updateAirport = async (
  id: number,
  airport: Omit<AirportDto, "id">
): Promise<AirportDto> => {
  const response = await axios.put<AirportDto>(`${BASE_URL}/${id}`, airport);
  return response.data;
};