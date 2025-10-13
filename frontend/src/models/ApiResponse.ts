export interface ApiResponse<T> {
  status: string;
  message: string;
  data: T;
  errorCode?: string;
  timestamp: string;
}
