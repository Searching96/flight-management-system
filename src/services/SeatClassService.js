import axios from "axios";

const REST_API_BASE_URL = 'http://localhost:8080/api/seat-classes';

export const listSeatClasses = () => axios.get(REST_API_BASE_URL);

export const addSeatClass = (seatClass) => axios.post(REST_API_BASE_URL, seatClass);

export const getSeatClass = (id) => axios.get(`${REST_API_BASE_URL}/${id}`);

export const updateSeatClass = (id, seatClass) => axios.put(`${REST_API_BASE_URL}/${id}`, seatClass);

//export const deleteSeatClass = (id) => axios.delete(`${REST_API_BASE_URL}/${id}`);
