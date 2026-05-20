package com.learning.hotelManagementSystem.security;

import com.learning.hotelManagementSystem.entity.User;
import com.learning.hotelManagementSystem.repository.UserRepository;
import com.learning.hotelManagementSystem.utils.AuthUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Slf4j
@Setter
@Getter
@AllArgsConstructor
@Component
public class JWTAuthFilter extends OncePerRequestFilter {
    private final UserRepository userRepository;
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final AuthUtil authUtil;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            final String requestTokenHeader=request.getHeader("Authorization");
//            log.info("Request in JWT Auth Filter:{}",request);
            if(requestTokenHeader==null || !requestTokenHeader.startsWith("Bearer")) {
                filterChain.doFilter(request,response);
                return;
            }
            String token=requestTokenHeader.split("Bearer ")[1];
            String username=authUtil.getUserNameFromToken(token);
            if(username!=null && SecurityContextHolder.getContext().getAuthentication()==null) {
                User user=userRepository.findUserByUserName(username).orElseThrow();
                UsernamePasswordAuthenticationToken token1=new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        user.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(token1);
                filterChain.doFilter(request,response);
            }
        } catch(Exception e) {
            handlerExceptionResolver.resolveException(request,response,null,e);
            throw e;
        }
    }
}
