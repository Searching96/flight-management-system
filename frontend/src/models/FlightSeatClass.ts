export interface FlightSeatClassDto {
    id: number;
    flightId: number;
    seatClassId: number;
    totalTickets: number;
    remainingTickets: number;
    currentPrice: number;
    deletedAt: Date;
}