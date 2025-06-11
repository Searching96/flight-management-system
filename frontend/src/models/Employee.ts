export interface Employee {
  employeeId?: number;
  accountName?: string;
  email?: string;
  citizenId?: string;
  phoneNumber?: string;
  employeeType: number;
  deletedAt?: string;
}

export interface UpdateEmployeeRequest {
  accountName?: string;
  email?: string;
  phoneNumber?: string;
  citizenId?: string;
  employeeType?: number;
}
