import axios from "axios";
import { DOMAIN_URL_DEFAULT, API_URL } from "./config";

const BASE_URL = `${DOMAIN_URL_DEFAULT}${API_URL.PARAMETERS}`;

export const getMaxMediumAirport = () => axios.get(`${BASE_URL}/max-medium-airport`);

export const getMinFlightDuration = () => axios.get(`${BASE_URL}/min-flight-duration`);

export const getMaxFlightDuration = () => axios.get(`${BASE_URL}/max-flight-duration`);

export const getMaxStopDuration = () => axios.get(`${BASE_URL}/max-stop-duration`);
