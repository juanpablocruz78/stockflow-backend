package com.stockflow.security.jwt;

import com.stockflow.security.service.CustomUserDetailsService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationEntryPoint entryPoint;

    public JwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsService userDetailsService, JwtAuthenticationEntryPoint entryPoint) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.entryPoint = entryPoint;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getServletPath().startsWith("/auth");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7);

        try{
            String username = jwtService.extractUsername(jwt);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails =
                        userDetailsService.loadUserByUsername(username);

                if (jwtService.isTokenValid(jwt, userDetails)) {

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

        filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            request.setAttribute("jwt_error", "TOKEN_EXPIRED");
            entryPoint.commence(
                    request,
                    response,
                    new InsufficientAuthenticationException("Token expirado", e)
            );

        } catch (MalformedJwtException e) {
            request.setAttribute("jwt_error", "TOKEN_MALFORMED");
            entryPoint.commence(
                    request,
                    response,
                    new InsufficientAuthenticationException("Token mal formado", e)
            );

        } catch (SignatureException e) {
            request.setAttribute("jwt_error", "TOKEN_SIGNATURE_INVALID");
            entryPoint.commence(
                    request,
                    response,
                    new InsufficientAuthenticationException("Firma inválida", e)
            );

        } catch (JwtException e) {
            request.setAttribute("jwt_error", "TOKEN_INVALID");
            entryPoint.commence(
                    request,
                    response,
                    new InsufficientAuthenticationException("Token inválido", e)
            );
        }
    }
}
