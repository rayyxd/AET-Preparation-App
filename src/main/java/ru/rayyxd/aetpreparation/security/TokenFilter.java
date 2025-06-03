package ru.rayyxd.aetpreparation.security;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.rayyxd.aetpreparation.sqlEntities.Student;
import ru.rayyxd.aetpreparation.sqlEntities.StudentDetailsImpl;
import ru.rayyxd.aetpreparation.sqlRepositories.StudentRepository;

@Component
public class TokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtCore jwtCore;

    // Заменяем UserDetailsService на StudentRepository, чтобы искать по ID
    @Autowired
    private StudentRepository studentRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String jwt = null;
        Long userId = null;

        try {
            String headerAuth = request.getHeader("Authorization");
            if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
                jwt = headerAuth.substring(7);
            }

            if (jwt != null) {
                try {
                    // Берём уже userId из поля sub
                    userId = jwtCore.getUserIdFromJwt(jwt);
                } catch (ExpiredJwtException e) {
                    // Токен просрочен
                    System.out.println("Received expired token");
                }

                // Если вытянули userId и в контексте нет авторизации — попробуем аутентифицировать
                if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                    Optional<Student> optStudent = studentRepository.findById(userId);
                    if (optStudent.isPresent()) {
                        Student student = optStudent.get();
                        // Собираем UserDetails из сущности:
                        UserDetails userDetails = StudentDetailsImpl.build(student);

                        // Создаём Authentication и кладём в контекст:
                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities());

                        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                    // Если студента не нашли — просто ничего не кладём в контекст, запрос упадёт дальше как неавторизованный
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        filterChain.doFilter(request, response);
    }
}
