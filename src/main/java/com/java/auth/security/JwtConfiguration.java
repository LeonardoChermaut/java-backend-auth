package com.java.auth.security;

import com.java.auth.service.DetalheUsuarioServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class JwtConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	private DetalheUsuarioServiceImpl servicoUsuario;

	@Autowired
	private PasswordEncoder codificador;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers(HttpMethod.POST, "/login" , "/user")
				.permitAll()
				.anyRequest()
				.authenticated()
				.and().addFilter(new JwtFilterAuthentication(authenticationManager()))
				.addFilter(new JwtFilterValidation(authenticationManager(), servicoUsuario)).
				sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		http.cors().and().csrf().disable();
	}

	@Bean
	CorsConfigurationSource sourceConfig() {
		CorsConfiguration configuracao = new CorsConfiguration();
		configuracao.setAllowedOrigins(Arrays.asList("http://localhost:3000/*" , "/**"));
		configuracao.setAllowedMethods(Arrays.asList("GET", "POST", "OPTIONS", "DELETE", "PUT", "PATCH"));
		configuracao.setAllowCredentials(true);
		configuracao.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuracao);
		return source;
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(servicoUsuario).passwordEncoder(codificador);
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/h2-console/**", "/swagger-ui/**", "/swagger-ui.html**", "/v2/api-docs",
				"/swagger-resources/**" , "localhost:8080/**" , "localhost:3000/**" );
	}

}