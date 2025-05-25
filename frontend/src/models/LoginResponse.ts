export interface LoginResponseDto {
  token: string;
  user: {
    id: number;
    email: string;
    accountName: string;
    accountType: number;
  };
  message?: string;
}
