export interface Account {
  accountId?: number;
  accountName: string;
  email: string;
  citizenId?: string;
  phoneNumber?: string;
  accountType: number; // 1 = customer, 2 = employee (matches backend)
  accountTypeName?: string;
}