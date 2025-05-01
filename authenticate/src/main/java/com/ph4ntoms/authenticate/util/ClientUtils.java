package com.ph4ntoms.authenticate.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class ClientUtils {
    public String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For"); // Header từ proxy/nginx
        if (ip != null && !ip.isEmpty()) {
            // Nếu có nhiều IP (proxy chain), lấy IP đầu tiên (IPv4)
            ip = ip.split(",")[0].trim();
        } else {
            ip = request.getHeader("X-Real-IP"); // Một số proxy sử dụng
            if (ip == null || ip.isEmpty()) {
                ip = request.getRemoteAddr(); // IP thật từ request
            }
        }

        // Nếu IP là IPv6 localhost (::1), chuyển thành 127.0.0.1
        if ("0:0:0:0:0:0:0:1".equals(ip)) {
            ip = "127.0.0.1";
        }
        return ip;
    }
}
