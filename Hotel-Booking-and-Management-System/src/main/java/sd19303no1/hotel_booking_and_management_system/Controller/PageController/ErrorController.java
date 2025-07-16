package sd19303no1.hotel_booking_and_management_system.Controller.PageController;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute("javax.servlet.error.status_code");
        Object message = request.getAttribute("javax.servlet.error.message");
        Object exception = request.getAttribute("javax.servlet.error.exception");
        
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            
            switch (statusCode) {
                case 400:
                    model.addAttribute("errorCode", "400");
                    model.addAttribute("errorTitle", "Yêu cầu không hợp lệ");
                    model.addAttribute("errorMessage", "Yêu cầu của bạn không hợp lệ. Vui lòng kiểm tra lại thông tin.");
                    break;
                case 404:
                    model.addAttribute("errorCode", "404");
                    model.addAttribute("errorTitle", "Trang không tìm thấy");
                    model.addAttribute("errorMessage", "Trang bạn đang tìm kiếm không tồn tại.");
                    break;
                case 500:
                    model.addAttribute("errorCode", "500");
                    model.addAttribute("errorTitle", "Lỗi máy chủ");
                    model.addAttribute("errorMessage", "Đã xảy ra lỗi nội bộ. Vui lòng thử lại sau.");
                    break;
                default:
                    model.addAttribute("errorCode", statusCode);
                    model.addAttribute("errorTitle", "Đã xảy ra lỗi");
                    model.addAttribute("errorMessage", "Đã xảy ra lỗi không xác định.");
                    break;
            }
        } else {
            model.addAttribute("errorCode", "Unknown");
            model.addAttribute("errorTitle", "Đã xảy ra lỗi");
            model.addAttribute("errorMessage", "Đã xảy ra lỗi không xác định.");
        }
        
        return "error";
    }
} 