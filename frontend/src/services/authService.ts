import { apiClient } from "./api";
import {
  LoginRequest,
  AuthResponse,
  UserDetails,
  RegisterRequest,
} from "../models";
import type { ApiResponse } from "../models/ApiResponse";

class AuthService {
  async login(credentials: LoginRequest): Promise<ApiResponse<AuthResponse>> {
    try {
      const response = await apiClient.post<ApiResponse<AuthResponse>>(
        "/auth/login",
        credentials
      );

      console.log(response); // Log the full response for debugging

      if (!response.data.userDetails) {
        throw new Error("Phản hồi không hợp lệ từ máy chủ");
      } else {
        this.setAuthData(response.data);
        return response;
      }
    } catch (error) {
      console.error("Login failed:", error);
      throw new Error(
        "Đăng nhập thất bại. Vui lòng kiểm tra thông tin đăng nhập."
      );
    }
  }

  async register(
    userData: RegisterRequest
  ): Promise<ApiResponse<AuthResponse>> {
    try {
      const response = await apiClient.post<ApiResponse<AuthResponse>>(
        "/auth/register",
        userData
      );

      console.log(response);

      if (!response.data.userDetails) {
        throw new Error("Phản hồi không hợp lệ từ máy chủ");
      } else {
        this.setAuthData(response.data);
        return response;
      }
    } catch (error) {
      console.error("Registration failed:", error);
      throw new Error("Đăng ký thất bại. Email có thể đã được sử dụng.");
    }
  }

  async createEmployee(
    userData: RegisterRequest
  ): Promise<ApiResponse<AuthResponse>> {
    try {
      const response = await apiClient.post<ApiResponse<AuthResponse>>(
        "/auth/create-employee",
        userData
      );

      console.log(response);

      if (!response.data.userDetails) {
        throw new Error("Phản hồi không hợp lệ từ máy chủ");
      } else {
        // this.setAuthData(response.data); // Do not log in the user upon employee creation
        return response;
      }
    } catch (error) {
      console.error("Employee creation failed:", error);
      throw new Error("Tạo nhân viên thất bại. Email có thể đã được sử dụng.");
    }
  }

  // Refresh access token
  async refreshToken(): Promise<ApiResponse<AuthResponse>> {
    const refreshToken = localStorage.getItem("refreshToken");
    if (!refreshToken) throw new Error("Không có refresh token");

    try {
      const response = await apiClient.post<ApiResponse<AuthResponse>>(
        "/auth/refresh",
        {
          token: refreshToken,
        }
      );

      console.log(response);

      if (!response.data.refreshToken) {
        throw new Error("Phản hồi không hợp lệ từ máy chủ");
      } else {
        this.setAuthData(response.data);
        return response;
      }
    } catch (error) {
      this.clearAuthData();
      console.error("Token refresh failed:", error);
      throw new Error("Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.");
    }
  }

  // Get current user details
  getCurrentUser(): UserDetails | null {
    const user = localStorage.getItem("user");
    return user ? JSON.parse(user) : null;
  }

  // Logout user
  logout(): void {
    this.clearAuthData();
    // Optional: Add API call to invalidate token on server
  }

  // Store auth data in localStorage
  private setAuthData(data: AuthResponse): void {
    localStorage.setItem("accessToken", data.accessToken);
    localStorage.setItem("refreshToken", data.refreshToken);
    localStorage.setItem("user", JSON.stringify(data.userDetails));
  }

  // Clear auth data from localStorage
  private clearAuthData(): void {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("refreshToken");
    localStorage.removeItem("user");
  }

  async validateResetToken(passwordResetToken: string): Promise<boolean> {
    try {
      const response = await apiClient.post<ApiResponse<boolean>>("/auth/validate", {
        token: passwordResetToken,
      });

      return response.data;
    } catch (error) {
      console.error("Token validation failed:", error);
      return false;
    }
  }

  async forgetPassword(email: string, phoneNumber?: string): Promise<void> {
    await apiClient
      .post("/auth/forget-password", { email, phoneNumber })
      .then(() => {
        console.log('Password reset email sent successfully');
      })
      .catch((error) => {
        console.error("Error sending password reset email:", error);
        throw error;
      });
  }

  async resetPassword(
    token: string,
    newPassword: string
  ): Promise<ApiResponse<AuthResponse>> {
    try {
      const response = await apiClient.post<ApiResponse<AuthResponse>>(
        "/auth/reset-password",
        { token, newPassword }
      );
      if (!response.data.userDetails) {
        throw new Error("Phản hồi không hợp lệ từ máy chủ");
      } else {
        this.clearAuthData();
        return response;
      }
    } catch (error) {
      console.error("Password reset failed:", error);
      throw new Error(
        "Đặt lại mật khẩu thất bại. Token không hợp lệ hoặc đã hết hạn."
      );
    }
  }
}

export const authService = new AuthService();
