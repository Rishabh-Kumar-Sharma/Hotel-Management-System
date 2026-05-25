package com.learning.hotelManagementSystem.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.hotelManagementSystem.entity.User;
import com.learning.hotelManagementSystem.exceptions.ApiError;
import com.learning.hotelManagementSystem.exceptions.ApiErrorCodesEnum;
import com.learning.hotelManagementSystem.exceptions.TokenExpirationException;
import com.learning.hotelManagementSystem.repository.UserRepository;
import com.learning.hotelManagementSystem.translations.Translations;
import com.learning.hotelManagementSystem.utils.AuthUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.nio.file.AccessDeniedException;

@Slf4j
@Setter
@Getter
@AllArgsConstructor
@Component
public class JWTAuthFilter extends OncePerRequestFilter {
    private final UserRepository userRepository;
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final AuthUtil authUtil;
    private final ObjectMapper objectMapper;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            final String requestTokenHeader=request.getHeader("Authorization");
//            log.info("Request in JWT Auth Filter:{}",request);
            if(requestTokenHeader==null || !requestTokenHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request,response);
                return;
            }
            String token=requestTokenHeader.split("Bearer ")[1];
            String username=authUtil.getUserNameFromToken(token);

            if(!authUtil.isTokenValid(token)) {
                throw new TokenExpirationException(Translations.TOKEN_EXPIRED);
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                User user = userRepository.findUserByUserName(username).orElseThrow();
                UsernamePasswordAuthenticationToken token1 = new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        user.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(token1);
            }
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");

            ApiError apiError=new ApiError(Translations.TOKEN_EXPIRED, ApiErrorCodesEnum.SESSION_TIMEOUT.name());

            response.getWriter().write(objectMapper.writeValueAsString(apiError));
        }
    }
}
