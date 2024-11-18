package ch.unil.doplab.studybuddy.ui;

import ch.unil.doplab.studybuddy.domain.Utils;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(filterName = "AuthenticationFilter", urlPatterns = {"*.xhtml"})
public class AuthenticationFilter implements Filter {
    {
        Utils.testModeOff();
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String requestURI = req.getRequestURI();

//        if (Utils.testMode() || requestURI.startsWith(req.getContextPath() + "/resources/")) {
         if (Utils.testMode() || requestURI.contains("resource")) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);
        boolean sessionExists = session != null;
        boolean validRequest = sessionExists && session.getAttribute("uuid") != null && session.getAttribute("username") != null;

        if (validRequest) {
            String role = (String) session.getAttribute("role");
            validRequest = role != null && (role.equals("student") || role.equals("teacher")) && requestURI.toLowerCase().contains(role);
        }
        String loginURI = req.getContextPath() + "/Login.xhtml";
        String registerURI = req.getContextPath() + "/Register.xhtml";
        boolean loginRequest = req.getRequestURI().equals(loginURI);
        boolean registerRequest = req.getRequestURI().equals(registerURI);

        if (loginRequest || registerRequest) {
            LoginBean.invalidateSession();
        }
        if (validRequest || loginRequest || registerRequest) {
            chain.doFilter(request, response);
        } else {
            res.sendRedirect(loginURI);
        }
    }
}