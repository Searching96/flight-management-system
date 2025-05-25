export interface Passenger {
  passengerId?: number;
  firstName: string;
  lastName: string;
  dateOfBirth: string;
  gender: 'Male' | 'Female' | 'Other';
  citizenId: string;
  phoneNumber: string;
  email: string;
}

export interface PassengerRequest {
  firstName: string;
  lastName: string;
  dateOfBirth: string;
  gender: string;
  citizenId: string;
  phoneNumber: string;
  email: string;
}
