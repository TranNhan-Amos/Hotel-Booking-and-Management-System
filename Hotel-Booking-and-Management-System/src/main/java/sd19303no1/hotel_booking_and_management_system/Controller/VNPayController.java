package sd19303no1.hotel_booking_and_management_system.Controller;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import sd19303no1.hotel_booking_and_management_system.Service.BookingOrderService;
import sd19303no1.hotel_booking_and_management_system.Service.VNPayService;
import sd19303no1.hotel_booking_and_management_system.Model.VNPayRequest;

@Controller
public class VNPayController {

    @Autowired
    private VNPayService vnPayService;

    @Autowired
private BookingOrderService bookingOrderService;


@PostMapping("/process-vnpay")
public ModelAndView processVNPayPayment(
        @RequestParam("amount") String amount,
        @RequestParam("bookingId") String orderId) {
    ModelAndView mv;
    try {
        if (amount == null || amount.trim().isEmpty()) {
            mv = new ModelAndView("error");
            mv.addObject("message", "Số tiền không hợp lệ: " + amount);
            return mv;
        }
        if (orderId == null || orderId.trim().isEmpty()) {
            mv = new ModelAndView("error");
           mv.addObject("message", "Thiếu mã đặt phòng: " + orderId);
            return mv;
        }
        String cleanAmount = amount.replaceAll("[^\\d]", "").trim();
        BigDecimal amountBigDecimal = new BigDecimal(cleanAmount);
        String formattedAmount = String.valueOf(amountBigDecimal.longValue());

        VNPayRequest vnPayRequest = new VNPayRequest();
        vnPayRequest.setAmount(formattedAmount);
        // String vnpTxnRef = String.valueOf(orderId) + "_" + System.currentTimeMillis();
        // vnPayRequest.setOrderId(vnpTxnRef);
        // System.out.println("vnp_TxnRef: " + vnpTxnRef);
        vnPayRequest.setOrderId(orderId);
        vnPayRequest.setReturnUrl("http://localhost:8080/vnpay-result");
        vnPayRequest.setIpAddr("127.0.0.1");
        vnPayRequest.setCreateDate(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));

        String rawOrderInfo = "Thanh toan don hang " + orderId;
        vnPayRequest.setOrderInfo(rawOrderInfo); // KHÔNG ENCODE!

        System.out.println("VNPay Params: " + vnPayRequest.getAmount() + ", " + vnPayRequest.getOrderId() + ", " + vnPayRequest.getReturnUrl());

        String paymentUrl = vnPayService.createPaymentRequest(vnPayRequest);
        return new ModelAndView("redirect:" + paymentUrl);
    } catch (Exception ex) {
        mv = new ModelAndView("error");
        mv.addObject("message", "Lỗi xử lý thanh toán: " + ex.getMessage());
        return mv;
    }
}
  @GetMapping("/vnpay-result")
public ModelAndView handleVNPayResponse(@RequestParam Map<String, String> params) {
    System.out.println("VNPay Response: " + params); // log toàn bộ params

    String responseCode = params.get("vnp_ResponseCode");
    System.out.println("Response Code: " + responseCode);
    String bookingIdStr = params.get("vnp_TxnRef");

    ModelAndView mv = new ModelAndView("VNPayResult");
    boolean isValid = vnPayService.validatePaymentResponse(params);

    if (bookingIdStr == null || !bookingIdStr.matches("\\d+")) {
        mv.addObject("message", "Không tìm thấy mã đặt phòng hợp lệ.");
        mv.addObject("isSuccess", false);
        return mv;
    }

    if (isValid && "00".equals(responseCode)) {
        try {
            // String bookingIdOrigin = bookingIdStr.split("_")[0];
            // bookingOrderService.confirmPayment(Integer.parseInt(bookingIdOrigin));
            int bookingId = Integer.parseInt(bookingIdStr); // Không cần split
            bookingOrderService.confirmPayment(bookingId);
            mv.addObject("message", "Thanh toán thành công!");
            mv.addObject("isSuccess", true);
            
        } catch (Exception e) {
            mv.addObject("message", "Xác nhận thanh toán lỗi: " + e.getMessage());
            mv.addObject("isSuccess", false);
        }
    } else {
        mv.addObject("message", "Thanh toán thất bại. Mã lỗi: " + responseCode + ", Chữ ký hợp lệ: " + isValid);
        mv.addObject("isSuccess", false);
    }
     mv.addObject("vnpTxnRef", params.get("vnp_TxnRef"));
    mv.addObject("bookingId", bookingIdStr.contains("_") ? bookingIdStr.split("_")[0] : bookingIdStr);
    mv.addObject("amount", formatAmount(params.get("vnp_Amount")));
    mv.addObject("paymentTime", formatPayDate(params.get("vnp_PayDate")));

    return mv;
}

private String formatAmount(String rawAmount) {
    if (rawAmount == null) return "0 ₫";
    try {
        long amount = Long.parseLong(rawAmount) / 100; // VNPay trả về số nhân 100
        return String.format("%,d ₫", amount); // format VND có dấu phẩy
    } catch (Exception e) {
        return rawAmount + " ₫";
    }
}

private String formatPayDate(String rawDate) {
    if (rawDate == null || rawDate.length() != 14) return "--/--/----";
    try {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date date = inputFormat.parse(rawDate);
        return outputFormat.format(date);
    } catch (Exception e) {
        return rawDate;
    }
}


}
