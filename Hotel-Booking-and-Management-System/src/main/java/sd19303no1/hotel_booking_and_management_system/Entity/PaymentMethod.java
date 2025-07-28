package sd19303no1.hotel_booking_and_management_system.Entity;

/**
 * Enum quản lý phương thức thanh toán
 */
public enum PaymentMethod {
    CREDIT_CARD("creditCard", "Thẻ tín dụng", true),
    MOMO("momo", "MoMo", true),
    BANK_TRANSFER("bankTransfer", "Chuyển khoản", true),
    PAY_ON_ARRIVAL("payOnArrival", "Thanh toán tại khách sạn", false);

    private final String code;
    private final String description;
    private final boolean requiresImmediatePayment;

    PaymentMethod(String code, String description, boolean requiresImmediatePayment) {
        this.code = code;
        this.description = description;
        this.requiresImmediatePayment = requiresImmediatePayment;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public boolean requiresImmediatePayment() {
        return requiresImmediatePayment;
    }

    public static PaymentMethod fromCode(String code) {
        for (PaymentMethod method : values()) {
            if (method.code.equals(code)) {
                return method;
            }
        }
        throw new IllegalArgumentException("Unknown payment method code: " + code);
    }
} 