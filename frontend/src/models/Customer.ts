export interface Customer {
  customerId?: number;
  accountName?: string;
  email?: string;
  citizenId?: string;
  phoneNumber?: string;
  score: number;
}

export interface Employee {
  employeeId?: number;
  accountName?: string;
  email?: string;
  phoneNumber?: string;
  employeeType: number;
  employeeTypeName?: string;
}
