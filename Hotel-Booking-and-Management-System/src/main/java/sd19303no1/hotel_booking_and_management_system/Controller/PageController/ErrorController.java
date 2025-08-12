package sd19303no1.hotel_booking_and_management_system.Controller.PageController;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/error")
public class ErrorController {

    @GetMapping("/403")
    public String accessDenied(Model model) {
        model.addAttribute("errorCode", "403");
        model.addAttribute("errorTitle", "Truy cập bị từ chối");
        model.addAttribute("errorMessage", "Bạn không có quyền truy cập vào trang này.");
        model.addAttribute("errorDescription", "Vui lòng đăng nhập với tài khoản có quyền truy cập hoặc liên hệ quản trị viên.");
        return "error";
    }

    @GetMapping("/404")
    public String notFound(Model model) {
        model.addAttribute("errorCode", "404");
        model.addAttribute("errorTitle", "Trang không tồn tại");
        model.addAttribute("errorMessage", "Trang bạn đang tìm kiếm không tồn tại.");
        model.addAttribute("errorDescription", "Có thể trang đã bị di chuyển hoặc xóa.");
        return "error";
    }

    @GetMapping("/500")
    public String serverError(Model model) {
        model.addAttribute("errorCode", "500");
        model.addAttribute("errorTitle", "Lỗi máy chủ");
        model.addAttribute("errorMessage", "Đã xảy ra lỗi trên máy chủ.");
        model.addAttribute("errorDescription", "Vui lòng thử lại sau hoặc liên hệ hỗ trợ.");
        return "error";
    }

    @GetMapping("/unauthorized")
    public String unauthorized(Model model) {
        model.addAttribute("errorCode", "401");
        model.addAttribute("errorTitle", "Chưa đăng nhập");
        model.addAttribute("errorMessage", "Bạn cần đăng nhập để truy cập trang này.");
        model.addAttribute("errorDescription", "Vui lòng đăng nhập để tiếp tục.");
        return "error";
    }
} 