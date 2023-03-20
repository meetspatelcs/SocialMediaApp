package net.mysite.SocialMedia.filter;

import net.mysite.SocialMedia.repository.UserRepository;
import net.mysite.SocialMedia.util.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Component
public class JWTFilter extends OncePerRequestFilter {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // gets authorization header and validate
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(!StringUtils.hasText(header) || StringUtils.hasText(header) && !header.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }

        // Authorization -> [Bearer], [asdf.2en4vrrtv.rtv4v3tv.vet4]
        final String token = header.split(" ")[1].trim();

        //gets Account identity and set it on the spring security context
        UserDetails userDetails = userRepository.findByUsername(jwtUtil.getUsernameFromToken(token)).orElse(null);

        // gets jwt token and validate
        if(!jwtUtil.validateToken(token, userDetails)){
            filterChain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails == null ? List.of() : userDetails.getAuthorities());
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // this is where the authentication occurs
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }
}
