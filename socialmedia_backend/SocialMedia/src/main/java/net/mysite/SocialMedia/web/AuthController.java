package net.mysite.SocialMedia.web;

import io.jsonwebtoken.ExpiredJwtException;
import net.mysite.SocialMedia.domain.User;
import net.mysite.SocialMedia.dto.AuthCredentialsRequest;
import net.mysite.SocialMedia.dto.UserDto;
import net.mysite.SocialMedia.service.InfoService;
import net.mysite.SocialMedia.service.UserService;
import net.mysite.SocialMedia.util.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTUtil jwtUtil;


    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody AuthCredentialsRequest request){
        try{
            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            User user = (User) authenticate.getPrincipal();

            user.setPassword(null);

            return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, jwtUtil.generateToken(user)).body(user);
        }
        catch(BadCredentialsException ex){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestParam String token, @AuthenticationPrincipal User user){
        try{
            Boolean isTokenValid = jwtUtil.validateToken(token, user);

            return ResponseEntity.ok(isTokenValid);
        }
        catch (ExpiredJwtException e){
            return ResponseEntity.ok(false);
        }
    }
}
