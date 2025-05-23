import axios, { AxiosResponse } from "axios";
import { DOMAIN_URL_DEFAULT, API_URL } from "./config";
import { SeatClassDto } from "../models/SeatClass";

const BASE_URL = `${DOMAIN_URL_DEFAULT}${API_URL.SEAT_CLASSES}`;

// Get all seat classes
export const listSeatClasses = async (): Promise<SeatClassDto[]> => {
  const response = await axios.get<SeatClassDto[]>(BASE_URL);
  return response.data;
}

// Add a new seat class
export const addSeatClass = async (
  seatClass: Omit<SeatClassDto, "id">
): Promise<SeatClassDto> => {
  const response = await axios.post<SeatClassDto>(BASE_URL, seatClass);
  return response.data;
}

// Get a seat class by ID
export const getSeatClass = async (
  id: number
): Promise<SeatClassDto> => {
  const response = await axios.get<SeatClassDto>(`${BASE_URL}/${id}`);
  return response.data;
}

// Update a seat class by ID
export const updateSeatClass = async (
  id: number,
  seatClass: Omit<SeatClassDto, "id">
): Promise<SeatClassDto> => {
  const response = await axios.put<SeatClassDto>(`${BASE_URL}/${id}`, seatClass);
  return response.data;
}