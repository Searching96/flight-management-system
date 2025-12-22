import { useState, useCallback } from "react";
import { Flight, FlightRequest, PaginatedResponse } from "../models";
import { flightService } from "../services";

export function useFlights() {
  const [flights, setFlights] = useState<Flight[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [currentPage, setCurrentPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  const loadFlights = useCallback(
    async (page: number = 0, size: number = 10) => {
      try {
        setLoading(true);
        const response = await flightService.getAllFlightsPaged(page, size);
        setFlights(response.data.content);
        setTotalPages(response.data.totalPages);
        setTotalElements(response.data.totalElements);
        setCurrentPage(response.data.number);
        setPageSize(size);
        setError("");
      } catch (err: any) {
        setError("Failed to load flights: " + (err.message || "Unknown error"));
      } finally {
        setLoading(false);
      }
    },
    []
  );

  const createFlight = useCallback(
    async (data: FlightRequest): Promise<Flight | null> => {
      try {
        const newFlight = await flightService.createFlight(data);
        await loadFlights(); // Refresh the list
        return newFlight.data;
      } catch (err: any) {
        setError(
          "Failed to create flight: " + (err.message || "Unknown error")
        );
        return null;
      }
    },
    [loadFlights]
  );

  const updateFlight = useCallback(
    async (flightId: number, data: FlightRequest): Promise<boolean> => {
      try {
        await flightService.updateFlight(flightId, data);
        await loadFlights(); // Refresh the list
        return true;
      } catch (err: any) {
        setError(
          "Failed to update flight: " + (err.message || "Unknown error")
        );
        return false;
      }
    },
    [loadFlights]
  );

  const deleteFlight = useCallback(
    async (flightId: number): Promise<boolean> => {
      try {
        await flightService.deleteFlight(flightId);
        await loadFlights(); // Refresh the list
        return true;
      } catch (err: any) {
        setError(
          "Failed to delete flight: " + (err.message || "Unknown error")
        );
        return false;
      }
    },
    [loadFlights]
  );

  return {
    flights,
    loading,
    error,
    loadFlights,
    createFlight,
    updateFlight,
    deleteFlight,
    clearError: () => setError(""),
    currentPage,
    pageSize,
    totalPages,
    totalElements,
    setCurrentPage,
    setPageSize,
  };
}
