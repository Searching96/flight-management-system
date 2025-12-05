import React, {
  createContext,
  useContext,
  useState,
  useEffect,
  useCallback,
  ReactNode,
} from "react";
import { LoginRequest, UserDetails, RegisterRequest } from "../models";
import { authService } from "../services/authService";

// Define the context type
interface AuthContextType {
  user: UserDetails | null;
  loading: boolean;
  login: (loginRequest: LoginRequest) => Promise<void>;
  register: (registerRequest: RegisterRequest) => Promise<void>;
  logout: () => void;
  refresh: () => Promise<void>;
  forgetPassword?: (email: string) => Promise<void>;
  resetPassword?: (token: string, newPassword: string) => Promise<void>;
}

// Create the context
const AuthContext = createContext<AuthContextType | undefined>(undefined);

// Define the provider component
interface AuthProviderProps {
  children: ReactNode;
}

/**
 * AuthProvider Component with Updated Permissions
 */
export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [user, setUser] = useState<UserDetails | null>(() => {
    const storedUser = authService.getCurrentUser();
    return storedUser ? storedUser : null;
  });

  // Add periodic token refresh
  useEffect(() => {
    const interval = setInterval(async () => {
      try {
        await authService.refreshToken();
      } catch (error) {
        console.error("Error refreshing token:", error);
        setUser(null);
      }
    }, 300000); // Refresh every 5 minutes

    return () => clearInterval(interval);
  }, []);

  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const storedUser = authService.getCurrentUser();
    if (storedUser) {
      setUser(storedUser);
    }
    setLoading(false);
  }, []);

  const login = useCallback(async (loginRequest: LoginRequest) => {
    setLoading(true);
    try {
      await authService.login(loginRequest);
      setUser(authService.getCurrentUser());
      setLoading(false);
      console.log(
        "User login successful at 2025-06-11 08:48:38 UTC by user"
      );
    } catch (err) {
      setLoading(false);
      throw err;
    }
  }, []);

  const register = useCallback(async (registerRequest: RegisterRequest) => {
    setLoading(true);
    try {
      await authService.register(registerRequest);
      setUser(authService.getCurrentUser());
      setLoading(false);
      console.log(
        "User registration successful at 2025-06-11 08:48:38 UTC by user"
      );
    } catch (err) {
      setLoading(false);
      throw err;
    }
  }, []);

  const logout = useCallback(() => {
    authService.logout();
    setUser(null);
    console.log("User logout at 2025-06-11 08:48:38 UTC by user");
  }, []);

  const refresh = useCallback(async () => {
    setLoading(true);
    try {
      await authService.refreshToken();
      setUser(authService.getCurrentUser());
      setLoading(false);
    } catch (err) {
      setLoading(false);
      setUser(null);
      throw err;
    }
  }, []);

  const forgetPassword = useCallback(async (email: string) => {
    setLoading(true);
    try {
      await authService.forgetPassword(email);
      setLoading(false);
    } catch (err) {
      setLoading(false);
      throw err;
    }
  }, []);

  const resetPassword = useCallback(
    async (token: string, newPassword: string) => {
      setLoading(true);
      try {
        await authService.resetPassword(token, newPassword);
        setLoading(false);
      } catch (err) {
        setLoading(false);
        throw err;
      }
    },
    []
  );

  const value = {
    user,
    loading,
    login,
    register,
    logout,
    refresh,
    forgetPassword,
    resetPassword,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

// Custom hook to use the auth context
export const useAuth = (): AuthContextType => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
};

/**
 * Updated Permissions Hook - Role-Based Access Control
 * Last updated: 2025-06-11 08:48:38 UTC by user
 */
export const usePermissions = () => {
  const { user } = useAuth();

  const hasRole = (role: string) => {
    if (!user) return false;
    return user.role === role;
  };

  const hasAnyRole = (roles: string[]) => {
    return roles.some((role) => hasRole(role));
  };

  return {
    // Customer permissions
    canSearchFlights: () =>
      !user || hasRole("CUSTOMER") || user?.accountTypeName === "Employee",
    canManageBookings: () =>
      !user ||
      hasRole("CUSTOMER") ||
      hasAnyRole([
        "EMPLOYEE_SUPPORT",
        "EMPLOYEE_TICKETING",
        "EMPLOYEE_ADMINISTRATOR",
      ]),
    canViewDashboard: () => hasRole("CUSTOMER"),

    // Updated Employee permissions based on corrected role structure
    canViewAdmin: () =>
      hasAnyRole(["EMPLOYEE_ADMINISTRATOR", "EMPLOYEE_SUPPORT"]),
    canViewFlightManagement: () =>
      hasAnyRole([
        "EMPLOYEE_FLIGHT_SCHEDULING",
        "EMPLOYEE_ADMINISTRATOR",
        "EMPLOYEE_SUPPORT",
      ]),
    canViewAirportManagement: () =>
      hasAnyRole([
        "EMPLOYEE_FLIGHT_OPERATIONS",
        "EMPLOYEE_ADMINISTRATOR",
        "EMPLOYEE_SUPPORT",
      ]),
    canViewPlaneManagement: () =>
      hasAnyRole([
        "EMPLOYEE_FLIGHT_OPERATIONS",
        "EMPLOYEE_ADMINISTRATOR",
        "EMPLOYEE_SUPPORT",
      ]),
    canViewTicketClassManagement: () =>
      hasAnyRole([
        "EMPLOYEE_FLIGHT_OPERATIONS",
        "EMPLOYEE_ADMINISTRATOR",
        "EMPLOYEE_SUPPORT",
      ]),
    canViewEmployeeManagement: () =>
      hasAnyRole(["EMPLOYEE_HUMAN_RESOURCES", "EMPLOYEE_ADMINISTRATOR"]),
    canViewParameterSettings: () =>
      hasAnyRole([
        "EMPLOYEE_FLIGHT_OPERATIONS",
        "EMPLOYEE_ADMINISTRATOR",
        "EMPLOYEE_SUPPORT",
      ]),
    canViewReports: () =>
      hasAnyRole(["EMPLOYEE_ACCOUNTING", "EMPLOYEE_ADMINISTRATOR"]),

    // Department-specific permissions
    canViewCustomerSupport: () =>
      hasAnyRole(["EMPLOYEE_SUPPORT", "EMPLOYEE_ADMINISTRATOR"]),
    canViewTicketing: () =>
      hasAnyRole(["EMPLOYEE_TICKETING", "EMPLOYEE_ADMINISTRATOR"]),
    canViewAccounting: () =>
      hasAnyRole(["EMPLOYEE_ACCOUNTING", "EMPLOYEE_ADMINISTRATOR"]),

    // General checks
    isCustomer: () => hasRole("CUSTOMER"),
    isEmployee: () => user?.accountTypeName === "Employee",
    isAdmin: () => hasRole("EMPLOYEE_ADMINISTRATOR"),

    // Specific role checks
    hasRole,
    hasAnyRole,
  };
};
