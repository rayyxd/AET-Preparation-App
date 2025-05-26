package ru.rayyxd.aetpreparation.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import ru.rayyxd.aetpreparation.services.StudentService;

@Configuration
@EnableWebSecurity
public class SecurityConfigurator {
	
	@Autowired
	private TokenFilter tokenFilter;
	
	@Autowired
	private StudentService studentService;
	
	public SecurityConfigurator() {}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
		return authenticationConfiguration.getAuthenticationManager();
	}
	 
	@Bean
	@Primary
	public AuthenticationManagerBuilder configureAuthenticationManagerBuilder(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception{
		authenticationManagerBuilder.userDetailsService(studentService).passwordEncoder(passwordEncoder());
		return authenticationManagerBuilder;
	}
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
		http
			.csrf(AbstractHttpConfigurer::disable)
			.cors(httpSecurityCorsConfigurer -> 
					httpSecurityCorsConfigurer.configurationSource(request ->
							new CorsConfiguration().applyPermitDefaultValues())
			)
			.exceptionHandling(exceptions -> exceptions
					.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
			)
			.sessionManagement(session -> session
					.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			)
			 .authorizeHttpRequests(auth -> auth
		                .requestMatchers("/login", "/register").permitAll()  // Разрешить без токена
		    //            .anyRequest().authenticated()                       // Все остальные требуют токен TODO: Убрать коммент перед продом
		                .anyRequest().permitAll()							 // На время разработки все роуты открыты TODO: Убрать строку перед продом 
			)
			.addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class);
		return http.build();

	}
	
}
