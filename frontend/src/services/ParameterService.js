import axios from "axios";
import { DOMAIN_URL_DEFAULT, API_URL } from "./config";

const BASE_URL = `${DOMAIN_URL_DEFAULT}${API_URL.PARAMETERS}`;

export const getMaxMediumAirport = () => axios.get(`${BASE_URL}/max-medium-airport`);

export const getMinFlightDuration = () => axios.get(`${BASE_URL}/min-flight-duration`);

export const getMaxFlightDuration = () => axios.get(`${BASE_URL}/max-flight-duration`);

export const getMaxStopDuration = () => axios.get(`${BASE_URL}/max-stop-duration`);

export const updateMaxMediumAirport = (maxAirportValue) =>
    axios.put(`${BASE_URL}/max-medium-airport`,
        { value: maxAirportValue } // Send as a JSON object
    );
    
export const updateMinFlightDuration = (minFlightDuration) => axios.put(`${BASE_URL}/min-flight-duration`, minFlightDuration);

export const updateMaxFlightDuration = (maxFlightDuration) => axios.put(`${BASE_URL}/max-flight-duration`, maxFlightDuration);

export const updateMaxStopDuration = (maxStopDuration) => axios.put(`${BASE_URL}/max-stop-duration`, maxStopDuration);

export const updateParameters = (params) => axios.put(BASE_URL, params);