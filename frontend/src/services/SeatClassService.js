import axios from "axios";

import { DOMAIN_URL_DEFAULT, API_URL } from "./config";

const BASE_URL = `${DOMAIN_URL_DEFAULT}${API_URL.SEAT_CLASSES}`;

export const listSeatClasses = () => axios.get(BASE_URL);

export const addSeatClass = (seatClass) => axios.post(BASE_URL, seatClass);

export const getSeatClass = (id) => axios.get(`${BASE_URL}/${id}`);

export const updateSeatClass = (id, seatClass) => axios.put(`${BASE_URL}/${id}`, seatClass);

