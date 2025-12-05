export interface PaymentResponse {
    code: string;  // Response code: "00" for success, "01" for error
    message: string;
    data?: string; // Payment URL for MoMo
    orderInfo?: string;
    orderCode: number | string;
    requestId?: string; // MoMo request ID
    amount?: number; // Payment amount
    qrCodeUrl?: string; // MoMo QR code URL for scanning
    deeplink?: string; // MoMo deeplink for mobile app
}

export interface PaymentReturnResponse {
    success: boolean; // Payment success status
    resultCode?: string; // MoMo result code
    message: string;
    orderId?: string; // MoMo order ID
    requestId?: string; // MoMo request ID
    amount?: string | number;
    transId?: string; // MoMo transaction ID
    payType?: string; // Payment type (qr, webApp, etc.)
    signatureValid: boolean;
    status?: string; // SUCCESS, FAILED, etc.
    description?: string; // Detailed status description
    timestamp?: string; // Payment timestamp
    
    // Payment fields
    code?: string;
    orderInfo?: string;
    transactionId?: string;
    bankCode?: string;
    cardType?: string;
    paymentDate?: string;
    transactionStatus?: string;
    data?: any;
}

export interface TransactionQueryResponse {
    success: boolean;
    orderId?: string;
    requestId?: string; // MoMo request ID
    amount?: number;
    resultCode?: string; // MoMo result code
    message?: string;
    transactionStatus?: string;
    transactionId?: string; // MoMo transaction ID (transId)
    
    // Response code for compatibility
    responseCode?: string;
}

export interface RefundResponse {
    success: boolean;
    orderId?: string;
    requestId?: string; // MoMo request ID
    refundAmount?: number;
    resultCode?: string; // MoMo result code
    message?: string;
    refundId?: string; // MoMo refund transaction ID
    
    // Response code for compatibility
    responseCode?: string;
}