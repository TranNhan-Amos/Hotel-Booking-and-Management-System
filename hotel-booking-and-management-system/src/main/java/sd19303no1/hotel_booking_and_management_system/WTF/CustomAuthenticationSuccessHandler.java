package sd19303no1.hotel_booking_and_management_system.WTF;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import sd19303no1.hotel_booking_and_management_system.Entity.SystemUserEntity;

import java.io.IOException;
import java.util.Collection;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        HttpSession session = request.getSession();
        SystemUserEntity userDetails = (SystemUserEntity) authentication.getPrincipal();
        session.setAttribute("loggedInUser", userDetails); // Lưu thông tin user vào session

        String targetUrl = determineTargetUrl(authentication);

        if (response.isCommitted()) {
            System.out.println("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }
        redirectStrategy.sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (GrantedAuthority grantedAuthority : authorities) {
            if (grantedAuthority.getAuthority().equals("ROLE_ADMIN")) {
                return "/admin/dashboard"; // Đường dẫn cho admin
            }
            // Thêm các điều kiện khác cho các vai trò khác nếu cần
            // else if (grantedAuthority.getAuthority().equals("ROLE_PARTNER")) {
            //     return "/partner/dashboard";
            // }
        }
        return "/"; // Đường dẫn mặc định cho các vai trò khác (ví dụ: CUSTOMER)
    }
}