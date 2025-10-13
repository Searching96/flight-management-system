import { useState, useCallback } from "react";
import { FlightDetail } from "../models";
import { flightDetailService } from "../services";

export type FlightDetailWithIndex = FlightDetail & { index: number };

export function useFlightDetails() {
  const [flightDetails, setFlightDetails] = useState<FlightDetailWithIndex[]>(
    []
  );
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string>("");

  const loadFlightDetails = useCallback(
    async (flightId: number): Promise<boolean> => {
      try {
        setLoading(true);
        console.log("Loading flight details for flight ID:", flightId);
        const details = await flightDetailService.getFlightDetailsById(
          flightId
        );
        console.log("Flight details loaded:", details);

        if (!details || details.data.length === 0) {
          console.warn("No flight details returned from API");
        }

        // Add an index property to each flight detail for tracking in UI
        const detailsWithIndex = details.data.map((detail, idx) => ({
          ...detail,
          index: idx + 1, // Using 1-based indexing for user display
        }));

        setFlightDetails(detailsWithIndex);
        setError("");
        return true;
      } catch (err: any) {
        console.error("Error loading flight details:", err);
        setError(
          "Không thể tải thông tin sân bay trung gian: " +
            (err.message || "Lỗi không xác định")
        );
        return false;
      } finally {
        setLoading(false);
      }
    },
    []
  );

  const saveFlightDetails = useCallback(
    async (flightId: number): Promise<boolean> => {
      try {
        // Only save if there are flight details
        if (flightDetails.length === 0) {
          return true; // No details to save, consider it success
        }

        console.log(
          `Preparing to save ${flightDetails.length} flight details for flight ID:`,
          flightId
        );

        // Filter out incomplete details before saving and remove UI-only fields
        const validDetailsToSave = flightDetails
          .filter(
            (detail) => detail.mediumAirportId !== 0 && detail.arrivalTime
          )
          .map(({ index, ...detailWithoutIndex }) => ({
            ...detailWithoutIndex,
            flightId: flightId, // Ensure correct flight ID
          }));

        console.log("Flight details to save:", validDetailsToSave);

        // First, get any existing flight details if updating
        const existingDetails = await flightDetailService.getFlightDetailsById(
          flightId
        );

        const existingDetailsData = existingDetails.data;

        try {
          // Process all operations in parallel
          await Promise.all([
            // Delete details that aren't in the new set
            ...existingDetailsData
              .filter(
                (existingDetail: { mediumAirportId: number }) =>
                  !validDetailsToSave.some(
                    (newDetail) =>
                      newDetail.mediumAirportId ===
                      existingDetail.mediumAirportId
                  )
              )
              .map((detail) => {
                console.log("Deleting flight detail:", detail);
                return flightDetailService.deleteFlightDetail(detail.flightId);
              }),

            // Update existing details
            ...validDetailsToSave
              .filter((newDetail) =>
                existingDetailsData.some(
                  (existingDetail) =>
                    existingDetail.mediumAirportId === newDetail.mediumAirportId
                )
              )
              .map((detail) => {
                console.log("Updating flight detail:", detail);
                const existingDetail = existingDetailsData.find(
                  (ed) => ed.mediumAirportId === detail.mediumAirportId
                );
                if (!existingDetail) return Promise.resolve();
                return flightDetailService.updateFlightDetail(
                  detail.flightId,
                  detail.mediumAirportId,
                  detail
                );
              }),

            // Create new details
            ...validDetailsToSave
              .filter(
                (newDetail) =>
                  !existingDetailsData.some(
                    (existingDetail) =>
                      existingDetail.mediumAirportId ===
                      newDetail.mediumAirportId
                  )
              )
              .map((detail) => {
                console.log("Creating flight detail:", detail);
                return flightDetailService.createFlightDetails(detail);
              }),
          ]);

          console.log("All flight details processed successfully");
        } catch (err) {
          console.error("Error processing flight details:", err);
          throw err;
        }

        return true;
      } catch (err: any) {
        console.error("Error saving flight details:", err);
        setError(
          "Không thể lưu thông tin sân bay trung gian: " +
            (err.message || "Lỗi không xác định")
        );
        return false;
      }
    },
    [flightDetails]
  );

  const addFlightDetail = useCallback((flightId: number = 0) => {
    setFlightDetails((prevDetails) => [
      ...prevDetails,
      {
        flightId,
        mediumAirportId: 0,
        arrivalTime: "",
        layoverDuration: 0,
        index: prevDetails.length + 1,
      },
    ]);
  }, []);

  const removeFlightDetail = useCallback((index: number) => {
    setFlightDetails((prevDetails) => {
      const newDetails = prevDetails.filter((_, i) => i !== index);
      return newDetails.map((item, i) => ({
        ...item,
        index: i + 1,
      }));
    });
  }, []);

  const updateFlightDetail = useCallback(
    (index: number, field: string, value: any) => {
      setFlightDetails((prevDetails) => {
        const newDetails = [...prevDetails];
        newDetails[index] = {
          ...newDetails[index],
          [field]: value,
        };
        return newDetails;
      });
    },
    []
  );

  return {
    flightDetails,
    setFlightDetails,
    loading,
    error,
    loadFlightDetails,
    saveFlightDetails,
    addFlightDetail,
    removeFlightDetail,
    updateFlightDetail,
    clearDetails: () => setFlightDetails([]),
    setError,
    clearError: () => setError(""),
  };
}
