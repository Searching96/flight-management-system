export interface Account {
  accountId?: number;
  accountName: string;
  password?: string;
  accountType: number; // 1 = admin, 2 = customer
  email?: string;
  phoneNumber?: string;
  citizenId?: string;
}

export interface LoginRequest {
  accountName: string;
  password: string;
}

export interface RegisterRequest {
  accountName: string;
  password: string;
  email: string;
  phoneNumber: string;
  citizenId: string;
  accountType: number;
}

export interface AuthResponse {
  account: Account;
  token?: string;
}
