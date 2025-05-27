export interface Passenger {
  passengerId?: number;
  passengerName: string;
  email: string;
  citizenId: string;
  phoneNumber: string;
}

export interface CreatePassengerRequest {
  passengerName: string;
  email: string;
  citizenId: string;
  phoneNumber: string;
}

export interface UpdatePassengerRequest {
  passengerName?: string;
  email?: string;
  phoneNumber?: string;
}
