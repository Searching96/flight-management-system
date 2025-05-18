import axios from "axios";

import { DOMAIN_URL_DEFAULT, API_URL } from "./config";

const BASE_URL = `${DOMAIN_URL_DEFAULT}${API_URL.AIRPORTS}`;

export const listAirports = () => axios.get(BASE_URL);

export const addAirport = (airport) => axios.post(BASE_URL, airport);

export const getAirport = (id) => axios.get(`${BASE_URL}/${id}`);

export const updateAirport = (id, airport) => axios.put(`${BASE_URL}/${id}`, airport);

