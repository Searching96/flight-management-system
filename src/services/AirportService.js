import axios from "axios";

const REST_API_BASE_URL = 'http://localhost:8080/api/airports';

export const listAirports = () => axios.get(REST_API_BASE_URL);

export const addAirport = (airport) => axios.post(REST_API_BASE_URL, airport);

export const getAirport = (id) => axios.get(`${REST_API_BASE_URL}/${id}`);
