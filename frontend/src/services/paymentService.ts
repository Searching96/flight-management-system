import { apiClient } from "./api";
import {
  PaymentResponse,
  PaymentReturnResponse,
  TransactionQueryResponse,
  RefundResponse,
  ApiResponse,
} from "../models";

class PaymentService {
  private readonly baseUrl = "/payment";

  /**
   * Create a payment request through MoMo
   * @param confirmationCode - Confirmation code for the booking
   * @returns Payment response with MoMo payment URL and additional data
   */
  async createPayment(confirmationCode: string): Promise<PaymentResponse> {
    try {
      const response = await apiClient.post<ApiResponse<any>>(
        `${this.baseUrl}/create?confirmationCode=${confirmationCode}`
      );

      // Access payment data from the ApiResponse structure
      console.log("MoMo payment creation response:", response.data);
      const paymentData = response.data.payment || response.data;
      
      return {
        code: paymentData.code || "00",
        message: paymentData.message || "success",
        data: paymentData.data, // MoMo payment URL
        orderInfo: paymentData.orderInfo,
        orderCode: paymentData.orderCode,
        requestId: paymentData.requestId,
        amount: paymentData.amount,
        qrCodeUrl: paymentData.qrCodeUrl, // MoMo QR code URL
        deeplink: paymentData.deeplink, // MoMo deeplink for mobile
      };
    } catch (error: any) {
      console.error("MoMo payment creation failed:", error);
      return {
        code: error.response?.data?.payment?.code || "99",
        message: error.response?.data?.payment?.message || "Failed to create MoMo payment",
        orderCode: 0,
      };
    }
  }

  /**
   * Process payment return from MoMo
   * @param queryParams - URL query parameters from MoMo return
   * @returns Payment processing result
   */
  async processPaymentReturn(
    queryParams: string
  ): Promise<PaymentReturnResponse> {
    try {
      const response = await apiClient.get<ApiResponse<any>>(
        `${this.baseUrl}/return${queryParams}`
      );
      
      const data = response.data;
      return {
        success: data.success || false,
        resultCode: data.resultCode,
        message: data.message,
        orderId: data.orderId,
        requestId: data.requestId,
        amount: data.amount,
        transId: data.transId,
        payType: data.payType,
        signatureValid: data.signatureValid || false,
        status: data.status,
        description: data.description,
        timestamp: data.timestamp,
      };
    } catch (error: any) {
      console.error("MoMo payment return processing failed:", error);
      return {
        success: false,
        resultCode: "99",
        message: error.response?.data?.message || "Failed to process MoMo payment return",
        signatureValid: false,
      };
    }
  }

  /**
   * Query MoMo transaction status
   * @param orderId - Order ID to query
   * @param transDate - Transaction date (not used by MoMo, kept for compatibility)
   * @returns Transaction query result
   */
  async queryTransaction(
    orderId: string,
    transDate?: string
  ): Promise<TransactionQueryResponse> {
    try {
      const response = await apiClient.post<ApiResponse<any>>(`${this.baseUrl}/query`, {
        params: {
          orderId,
          transDate, // MoMo doesn't require this but kept for API compatibility
        },
      });

      const data = response.data;
      return {
        success: data.success || false,
        orderId: data.orderId,
        requestId: data.requestId,
        amount: data.amount,
        resultCode: data.resultCode,
        message: data.message,
        transactionStatus: data.resultCode === "0" ? "SUCCESS" : "FAILED",
        transactionId: data.transId,
      };
    } catch (error: any) {
      console.error("MoMo transaction query failed:", error);
      return {
        success: false,
        message: error.response?.data?.message || "Failed to query MoMo transaction",
      };
    }
  }

  /**
   * Refund a MoMo transaction
   * @param orderId - Original order ID
   * @param amount - Refund amount in VND
   * @param transDate - Original transaction date (for compatibility)
   * @param user - User performing the refund
   * @param transType - Transaction type (not used by MoMo)
   * @returns Refund processing result
   */
  async refundTransaction(
    orderId: string,
    amount: number,
    transDate: string,
    user: string,
    transType: string = "02"
  ): Promise<RefundResponse> {
    try {
      const response = await apiClient.post<ApiResponse<any>>(`${this.baseUrl}/refund`, {
        params: {
          orderId,
          amount: amount.toString(),
          transDate, // For API compatibility
          user,
          transType,
        },
      });

      const data = response.data;
      return {
        success: data.success || false,
        orderId: data.orderId,
        requestId: data.requestId,
        refundAmount: amount,
        resultCode: data.resultCode,
        message: data.message,
        refundId: data.transId,
      };
    } catch (error: any) {
      console.error("MoMo refund failed:", error);
      return {
        success: false,
        message: error.response?.data?.message || "Failed to process MoMo refund",
      };
    }
  }

  /**
   * Helper method to format date for transaction reference
   * @param date - Date to format
   * @returns Formatted date string (YYYYMMDDHHMMSS)
   */
  formatTransactionDate(date: Date = new Date()): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");
    const hours = String(date.getHours()).padStart(2, "0");
    const minutes = String(date.getMinutes()).padStart(2, "0");
    const seconds = String(date.getSeconds()).padStart(2, "0");

    return `${year}${month}${day}${hours}${minutes}${seconds}`;
  }

  /**
   * Open MoMo payment page
   * @param paymentUrl - MoMo payment URL
   * @param deeplink - MoMo deeplink for mobile devices
   */
  openMoMoPayment(paymentUrl: string, deeplink?: string): void {
    // Check if on mobile device
    const isMobile = /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(
      navigator.userAgent
    );

    if (isMobile && deeplink) {
      // Try to open MoMo app via deeplink
      window.location.href = deeplink;
      
      // Fallback to web payment after a short delay
      setTimeout(() => {
        window.open(paymentUrl, '_self');
      }, 2000);
    } else {
      // Open web payment page
      window.open(paymentUrl, '_self');
    }
  }

  /**
   * Generate QR code display URL for desktop users
   * @param qrCodeUrl - MoMo QR code URL
   * @returns QR code image URL
   */
  getQRCodeUrl(qrCodeUrl: string): string {
    return qrCodeUrl;
  }

  /**
   * Get payment status for a confirmation code
   * @param confirmationCode - Booking confirmation code
   * @returns Payment status information
   */
  async getPaymentStatus(confirmationCode: string): Promise<{
    success: boolean;
    confirmationCode: string;
    totalTickets: number;
    paidTickets: number;
    unpaidTickets: number;
    bookingPaid: boolean;
    partiallyPaid: boolean;
    totalAmount: number;
    paidAmount: number;
    unpaidAmount: number;
    paymentRequired: boolean;
    message?: string;
  }> {
    try {
      const response = await apiClient.get<ApiResponse<any>>(
        `${this.baseUrl}/status/${confirmationCode}`
      );
      
      return response.data;
    } catch (error: any) {
      console.error("Payment status check failed:", error);
      return {
        success: false,
        confirmationCode,
        totalTickets: 0,
        paidTickets: 0,
        unpaidTickets: 0,
        bookingPaid: false,
        partiallyPaid: false,
        totalAmount: 0,
        paidAmount: 0,
        unpaidAmount: 0,
        paymentRequired: false,
        message: error.response?.data?.message || "Failed to get payment status",
      };
    }
  }

  /**
   * Cancel payment for a confirmation code
   * @param confirmationCode - Booking confirmation code
   * @returns Cancellation result
   */
  async cancelPayment(confirmationCode: string): Promise<{
    success: boolean;
    message: string;
    confirmationCode: string;
    cancelledTickets?: number;
    requiresRefund?: boolean;
    timestamp?: string;
  }> {
    try {
      const response = await apiClient.post<ApiResponse<any>>(
        `${this.baseUrl}/cancel/${confirmationCode}`
      );
      
      return response.data;
    } catch (error: any) {
      console.error("Payment cancellation failed:", error);
      return {
        success: false,
        message: error.response?.data?.message || "Failed to cancel payment",
        confirmationCode,
      };
    }
  }
}

export const paymentService = new PaymentService();
