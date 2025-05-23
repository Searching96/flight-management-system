export interface FlightSeatClass {
    id: number;
    flightId: number;
    seatClassId: number;
    totalTicket: number;
    remainingTicket: number;
    currentPrice: number;
    deletedAt: Date;
}