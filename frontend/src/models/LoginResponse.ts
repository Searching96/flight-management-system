export interface LoginResponseDto {
  accountId: number;
  accountName: string;
  email: string;
  accountType: number;
  token: string | null;
  message?: string;
}
