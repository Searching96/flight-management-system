export interface Employee {
  employeeId?: number;
  accountName?: string;
  email?: string;
  phoneNumber?: string;
  employeeType: number;
  employeeTypeName?: string;
}

export interface CreateEmployeeRequest {
  accountName: string;
  email: string;
  phoneNumber: string;
  citizenId: string;
  employeeType: number;
  password: string;
}

export interface UpdateEmployeeRequest {
  accountName?: string;
  email?: string;
  phoneNumber?: string;
  employeeType?: number;
}

export interface EmployeeLoginRequest {
  email: string;
  password: string;
}

export interface EmployeeLoginResponse {
  token: string;
  employee: Employee;
}

export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
}

export interface EmployeeSearchFilters {
  employeeType?: number;
  keyword?: string;
}
