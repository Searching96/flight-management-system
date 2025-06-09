export interface Employee {
  employeeId?: number;
  accountName?: string;
  email?: string;
  phoneNumber?: string;
  employeeType: number;
  employeeTypeName?: string;
}

export interface UpdateEmployeeRequest {
  accountName?: string;
  email?: string;
  phoneNumber?: string;
  employeeType?: number;
}

export interface EmployeeSearchFilters {
  employeeType?: number;
  keyword?: string;
}
