package com.techg.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/** Sends anyone who is not logged in to the admin login page. Visitors of the public site are never affected. */
@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    public static final String SESSION_KEY = "TECHG_ADMIN";

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
        HttpSession session = req.getSession(false);
        if (session != null && Boolean.TRUE.equals(session.getAttribute(SESSION_KEY))) {
            return true;
        }
        res.sendRedirect(req.getContextPath() + "/admin/login");
        return false;
    }
}
