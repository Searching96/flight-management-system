export interface PaymentResponse {
    code: string;  // Changed from number to string to match "00" format
    message: string;
    data?: string;
    orderInfo?: string;
    orderCode: number;
}

export interface PaymentReturnResponse {
    code: string;
    message: string;
    signatureValid: boolean;
    amount?: string;
    orderId?: string;
    orderInfo?: string;
    transactionId?: string;
    bankCode?: string;
    cardType?: string;
    paymentDate?: string;
    transactionStatus?: string;
    data?: any; // Optionally keep this for all raw fields
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