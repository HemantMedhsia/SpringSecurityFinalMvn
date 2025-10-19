package com.hemant.springsecurityfinalmvn.config.security;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    @SuppressWarnings("resource")
	@Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String token = null;
        if(request.getCookies() != null) {
            for(Cookie cookie : request.getCookies()) {
                if("access_token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }
        if(token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String username;
         try {
             username = jwtService.extractUsername(token);
         } catch (ExpiredJwtException e) {
             response.setStatus(HttpStatus.UNAUTHORIZED.value());
             response.setContentType("application/json");
             response.getWriter().write("{\"status\":\"fail\",\"message\":\"Token expired\"}");
             return;
        }  catch (Exception e) {
             response.setStatus(HttpStatus.UNAUTHORIZED.value());
             response.setContentType("application/json");
             response.getWriter().write("{\"status\":\"fail\",\"message\":\"Invalid token\"}");
             return;
        }

         if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
             UserDetails user = userDetailsService.loadUserByUsername(username);
             if(jwtService.isTokenValid(token, user)) {
                 UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                         user, null, user.getAuthorities());
                 auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                 SecurityContextHolder.getContext().setAuthentication(auth);
             }
         }
        filterChain.doFilter(request, response);
    }
}