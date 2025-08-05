package sd19303no1.hotel_booking_and_management_system.Model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VNPayRequest {
    private String version;
    private String command;
    private String merchantCode;
    private String orderId;
    private String amount;
    private String currency;
    private String redirectUrl;
    private String ipAddress;
    private String orderInfo;
    private String requestId;
    private String signature;
    private String returnUrl;
    private String ipAddr;
    private String createDate;

    public VNPayRequest(String version, String command, String merchantCode, String orderId, String amount, String currency, String redirectUrl, String ipAddress, String orderInfo, String requestId, String signature) {
        this.version = version;
        this.command = command;
        this.merchantCode = merchantCode;
        this.orderId = orderId;
        this.amount = amount;
        this.currency = currency;
        this.redirectUrl = redirectUrl;
        this.ipAddress = ipAddress;
        this.orderInfo = orderInfo;
        this.requestId = requestId;
        this.signature = signature;
        this.returnUrl = redirectUrl; 
        this.ipAddr = ipAddress; 
        this.createDate = java.time.LocalDateTime.now().toString(); 
    }

    // Getters and Setters
    public VNPayRequest() {
    // constructor rỗng
    }
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getOrderInfo() {
        return orderInfo;
    }

    public void setOrderInfo(String orderInfo) {
        this.orderInfo = orderInfo;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
    public String getReturnUrl() {
        return returnUrl;
    }
}