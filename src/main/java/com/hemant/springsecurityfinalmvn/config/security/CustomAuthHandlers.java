//package com.hemant.springsecurityfinalmvn.config.security;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.access.AccessDeniedException;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.web.AuthenticationEntryPoint;
//import org.springframework.security.web.access.AccessDeniedHandler;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//
//@Component
//public class CustomAuthHandlers implements AuthenticationEntryPoint, AccessDeniedHandler {
//    private void writeJson(HttpServletResponse response, int status, String message) throws IOException {
//        response.setStatus(status);
//        response.setContentType("application/json");
//        Map<String, Object> body = new HashMap<>();
//        body.put("status", "fail");
//        body.put("message", message);
//        new ObjectMapper().writeValue(response.getOutputStream(), body);
//    }
//
//    @Override
//    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException ex)
//            throws IOException {
//        writeJson(response, HttpStatus.FORBIDDEN.value(), "Access denied: insufficient permissions");
//    }
//
//    @Override
//    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
//        writeJson(response, HttpStatus.UNAUTHORIZED.value(), "Unauthorized: Missing or invalid token");
//    }
//}



package com.hemant.springsecurityfinalmvn.config.security;
 
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
 
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
 
@Component
public class CustomAuthHandlers implements AuthenticationEntryPoint, AccessDeniedHandler {
 
    private void addCorsHeaders(HttpServletResponse response) {
        // ✅ Allow your frontend origin
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        response.setHeader("Access-Control-Allow-Credentials", "true");
 
        // ✅ Include required headers for browser’s CORS validation
        response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, Accept");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
    }
 
    private void writeJson(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
 
        Map<String, Object> body = new HashMap<>();
        body.put("status", "fail");
        body.put("message", message);
 
        new ObjectMapper().writeValue(response.getOutputStream(), body);
    }
 
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException ex)
            throws IOException {
        addCorsHeaders(response); // 👈 Added this
        writeJson(response, HttpStatus.FORBIDDEN.value(), "Access denied: insufficient permissions");
    }
 
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {
        addCorsHeaders(response); // 👈 Added this
        writeJson(response, HttpStatus.UNAUTHORIZED.value(), "Unauthorized: Missing or invalid token");
    }
}
