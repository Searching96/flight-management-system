export interface Account {
  accountId?: number;
  accountName: string;
  email: string;
  citizenId?: string;
  phoneNumber?: string;
  accountType: number; // 1 = customer, 2 = employee (matches backend)
  accountTypeName?: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  accountId: number;
  accountName: string;
  email: string;
  accountType: number;
  token?: string;
}

export interface RegisterRequest {
  accountName: string;
  password: string;
  email: string;
  citizenId: string;
  phoneNumber: string;
  accountType: number;
}
