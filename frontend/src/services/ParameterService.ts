import axios from "axios";
import { ParameterDto } from "../models/Parameter";
import { DOMAIN_URL_DEFAULT, API_URL } from "./config";

const BASE_URL = `${DOMAIN_URL_DEFAULT}${API_URL.PARAMETERS}`;

export const getParameter = async (): Promise<ParameterDto> => {
  const response = await axios.get<ParameterDto>(BASE_URL);
  console.log(response.data);
  return response.data;
};

export const updateParameter = async (
  parameter : ParameterDto
): Promise<ParameterDto> => {
  const response = await axios.put<ParameterDto>(`${BASE_URL}`, parameter);
  return response.data;
};