import { apiClient } from './api';

export interface PaymentResponse {
    success: boolean;
    paymentUrl?: string;
    message?: string;
    data?: any;
}

export interface PaymentReturnResponse {
    success: boolean;
    orderId?: string;
    amount?: number;
    responseCode?: string;
    message?: string;
    transactionId?: string;
}

export interface TransactionQueryResponse {
    success: boolean;
    orderId?: string;
    amount?: number;
    responseCode?: string;
    message?: string;
    transactionStatus?: string;
    transactionId?: string;
}

export interface RefundResponse {
    success: boolean;
    orderId?: string;
    refundAmount?: number;
    responseCode?: string;
    message?: string;
    refundId?: string;
}

class PaymentService {
    private readonly baseUrl = '/payment';

    /**
     * Create a payment request
     * @param amount - Payment amount in VND
     * @returns Payment response with payment URL
     */
    async createPayment(amount: number): Promise<PaymentResponse> {
        try {
            const response = await apiClient.post(`${this.baseUrl}/create`, {
                params: {
                    amount: amount.toString()
                }
            });

            return {
                success: true,
                paymentUrl: response.data.paymentUrl,
                message: response.data.message,
                data: response.data
            };
        } catch (error: any) {
            console.error('Payment creation failed:', error);
            return {
                success: false,
                message: error.response?.data?.message || 'Failed to create payment'
            };
        }
    }

    /**
     * Process payment return from VNPay
     * @param queryParams - URL query parameters from VNPay return
     * @returns Payment processing result
     */
    async processPaymentReturn(queryParams: URLSearchParams): Promise<PaymentReturnResponse> {
        try {
            const response = await apiClient.get(`${this.baseUrl}/return`, {
                params: Object.fromEntries(queryParams.entries())
            });

            return {
                success: response.data.success || false,
                orderId: response.data.orderId,
                amount: response.data.amount,
                responseCode: response.data.responseCode,
                message: response.data.message,
                transactionId: response.data.transactionId
            };
        } catch (error: any) {
            console.error('Payment return processing failed:', error);
            return {
                success: false,
                message: error.response?.data?.message || 'Failed to process payment return'
            };
        }
    }

    /**
     * Query transaction status
     * @param orderId - Order ID to query
     * @param transDate - Transaction date (YYYYMMDDHHMMSS format)
     * @returns Transaction query result
     */
    async queryTransaction(orderId: string, transDate: string): Promise<TransactionQueryResponse> {
        try {
            const response = await apiClient.post(`${this.baseUrl}/query`, {
                params: {
                    orderId,
                    transDate
                }
            });

            return {
                success: response.data.success || false,
                orderId: response.data.orderId,
                amount: response.data.amount,
                responseCode: response.data.responseCode,
                message: response.data.message,
                transactionStatus: response.data.transactionStatus,
                transactionId: response.data.transactionId
            };
        } catch (error: any) {
            console.error('Transaction query failed:', error);
            return {
                success: false,
                message: error.response?.data?.message || 'Failed to query transaction'
            };
        }
    }

    /**
     * Refund a transaction
     * @param orderId - Original order ID
     * @param amount - Refund amount in VND
     * @param transDate - Original transaction date (YYYYMMDDHHMMSS format)
     * @param user - User performing the refund
     * @param transType - Transaction type (default: "02" for full refund)
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
            const response = await apiClient.post(`${this.baseUrl}/refund`, {
                params: {
                    orderId,
                    amount: amount.toString(),
                    transDate,
                    user,
                    transType
                }
            });

            return {
                success: response.data.success || false,
                orderId: response.data.orderId,
                refundAmount: response.data.refundAmount,
                responseCode: response.data.responseCode,
                message: response.data.message,
                refundId: response.data.refundId
            };
        } catch (error: any) {
            console.error('Refund failed:', error);
            return {
                success: false,
                message: error.response?.data?.message || 'Failed to process refund'
            };
        }
    }

    /**
     * Helper method to format date for VNPay API
     * @param date - Date to format
     * @returns Formatted date string (YYYYMMDDHHMMSS)
     */
    formatTransactionDate(date: Date = new Date()): string {
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        const hours = String(date.getHours()).padStart(2, '0');
        const minutes = String(date.getMinutes()).padStart(2, '0');
        const seconds = String(date.getSeconds()).padStart(2, '0');

        return `${year}${month}${day}${hours}${minutes}${seconds}`;
    }

    /**
     * Generate unique order ID
     * @param prefix - Optional prefix for order ID
     * @returns Unique order ID
     */
    generateOrderId(prefix: string = 'ORDER'): string {
        const timestamp = Date.now();
        const random = Math.floor(Math.random() * 1000).toString().padStart(3, '0');
        return `${prefix}${timestamp}${random}`;
    }
}

export const paymentService = new PaymentService();
