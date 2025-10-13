export interface LoginRequest {
  email: string;
  password: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: Date;
  userDetails: UserDetails;
}

export interface UserDetails {
  id: number;
  accountName: string;
  email: string;
  role: string;
  accountTypeName: string;
}

export interface RegisterRequest {
  accountName: string;
  password?: string; // Make optional since backend generates it for employees
  email: string;
  citizenId: string;
  phoneNumber: string;
  accountType: number;
  employeeType?: number;
}
