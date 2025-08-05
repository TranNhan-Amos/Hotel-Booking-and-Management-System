package sd19303no1.hotel_booking_and_management_system.Service;

import sd19303no1.hotel_booking_and_management_system.Model.VNPayRequest;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VNPayService {
    private final String vnpUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    private final String secretKey = "4DME1Y016LDYETZCPDX3DW8PY9FRYRQJ"; // Đúng key sandbox
    private final String merchantId = "GD8UNVD3"; // Đúng TMNCode sandbox

    public String createPaymentRequest(VNPayRequest vnPayRequest) {
        Map<String, String> params = new HashMap<>();
        params.put("vnp_Version", "2.0.0");
        params.put("vnp_Command", "pay");
        params.put("vnp_TmnCode", merchantId);
        params.put("vnp_Amount", String.valueOf(Long.parseLong(vnPayRequest.getAmount()) * 100));
        params.put("vnp_CurrCode", "VND");
        params.put("vnp_TxnRef", vnPayRequest.getOrderId());
        params.put("vnp_OrderInfo", vnPayRequest.getOrderInfo());
        params.put("vnp_OrderType", "other"); // Có thể để "other", "billpayment",...
        params.put("vnp_ReturnUrl", "http://localhost:8080/vnpay-result");
        params.put("vnp_Locale", "vn");
        params.put("vnp_IpAddr", vnPayRequest.getIpAddr());
        params.put("vnp_CreateDate", vnPayRequest.getCreateDate());
        params.put("vnp_BankCode", "NCB");
        params.put("vnp_SecureHashType", "HmacSHA512");

        // Bỏ 2 param này ra khi tạo chữ ký
        String secureHash = generateSecureHash(params);
        params.put("vnp_SecureHash", secureHash);

        // Build URL - key và value đều encode UTF-8
        StringBuilder paymentUrl = new StringBuilder(vnpUrl).append("?");
        params.forEach((key, value) -> {
            paymentUrl.append(URLEncoder.encode(key, StandardCharsets.UTF_8));
            paymentUrl.append("=");
            paymentUrl.append(URLEncoder.encode(value, StandardCharsets.UTF_8));
            paymentUrl.append("&");
        });
        paymentUrl.deleteCharAt(paymentUrl.length() - 1);

        System.out.println("VNPay URL: " + paymentUrl); // Debug

        return paymentUrl.toString();
    }

    private String generateSecureHash(Map<String, String> inputParams) {
        try {
            SortedMap<String, String> sortedParams = new TreeMap<>(inputParams);
            sortedParams.remove("vnp_SecureHash");
            sortedParams.remove("vnp_SecureHashType");

            StringBuilder data = new StringBuilder();
            for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
                data.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            data.deleteCharAt(data.length() - 1);
            System.out.println("Chuỗi để hash: " + data.toString());
            System.out.println("SecretKey: " + secretKey);

            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            Mac mac = Mac.getInstance("HmacSHA512");
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(data.toString().getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi tạo chữ ký bảo mật", e);
        }
    }

   public boolean validatePaymentResponse(Map<String, String> params) {
    String secureHash = params.get("vnp_SecureHash");
    if (secureHash == null) {
        System.out.println("Missing vnp_SecureHash");
        return false;
    }

    String secretKey = "4DME1Y016LDYETZCPDX3DW8PY9FRYRQJ"; // Đúng key sandbox
    String hashData = params.entrySet().stream()
        .filter(entry -> !entry.getKey().equals("vnp_SecureHash"))
        .sorted(Map.Entry.comparingByKey())
        .map(entry -> {
            try {
                return entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.toString());
            } catch (Exception e) {
                System.err.println("Error encoding param: " + entry.getKey());
                return "";
            }
        })
        .collect(Collectors.joining("&"));

    String calculatedHash = hmacSHA512(secretKey, hashData);
    System.out.println("Hash Data: " + hashData);
    System.out.println("Secure Hash from VNPay: " + secureHash);
    System.out.println("Calculated Hash: " + calculatedHash);

    return secureHash.equalsIgnoreCase(calculatedHash);
}

private String hmacSHA512(String key, String data) {
    try {
        Mac mac = Mac.getInstance("HmacSHA512");
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
        mac.init(keySpec);
        byte[] hmacBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder result = new StringBuilder();
        for (byte b : hmacBytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    } catch (Exception e) {
        System.err.println("Error calculating HMAC SHA512: " + e.getMessage());
        return "";
    }
}
}
