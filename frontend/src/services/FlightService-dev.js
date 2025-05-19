// src/services/api.js
const API_DOMAIN = 'http://localhost:8080/';

async function fetchData(endpoint, method = 'GET', body = null) {
  const url = `${API_DOMAIN}${endpoint}`;
  const headers = {
    'Content-Type': 'application/json',
  };
  const config = {
    method,
    headers,
  };
  if (body) {
    config.body = JSON.stringify(body);
  }

  const response = await fetch(url, config);
  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.error || `HTTP error! status: ${response.status}`);
  }
  return response.json();
}

export const getListFlights = () => fetchData('api/flights');
// export const getDaily = (maDaiLy) => fetchData(`/daily/${maDaiLy}`);
export const addFlight = (data) => fetchData('api/flights', 'POST', data);
// export const updateDaily = (maDaiLy, data) => fetchData(`/daily/${maDaiLy}`, 'PUT', data);
// export const deleteDaily = (maDaiLy) => fetchData(`/daily/${maDaiLy}`, 'DELETE');

export const listAirports = () => fetchData('api/airports');