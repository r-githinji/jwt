package com.auth.example.jwt;

import java.util.List;
import java.util.stream.Collectors;
import java.time.format.DateTimeFormatter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.http.HttpMethod;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

@SpringBootApplication @EnableWebSecurity
public class JwtApplication {

	public static void main(String[] args) {
		SpringApplication.run(JwtApplication.class, args);
	}
	
	@Bean
	public Jackson2ObjectMapperBuilderCustomizer jacksonMapper() {
		return builder -> {
			builder.simpleDateFormat(Constants.SIMPLE_DATE_TIME_FORMAT);
			DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(Constants.SIMPLE_DATE_FORMAT),
					dateTimeFormat = DateTimeFormatter.ofPattern(Constants.SIMPLE_DATE_TIME_FORMAT);			
			builder.serializers(new LocalDateSerializer(dateFormat),
					new LocalDateTimeSerializer(dateTimeFormat));			
			builder.deserializers(new LocalDateDeserializer(dateFormat), 
					new LocalDateTimeDeserializer(dateTimeFormat));
		};
	}
	
	@Bean
	public MessageSource messageSource() {
		ReloadableResourceBundleMessageSource ms = new ReloadableResourceBundleMessageSource();
		ms.setDefaultEncoding("UTF-8");
		ms.setBasename("classpath:ValidationMessages");
		return ms;
	}
	
	@Bean
	public LocalValidatorFactoryBean validatorFactory() {
		LocalValidatorFactoryBean lvfb = new LocalValidatorFactoryBean();
		lvfb.setValidationMessageSource(messageSource());
		return lvfb;
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		return encoder;
	}
	
	@Bean
	public AuthenticationManager authenticationManagerBean(HttpSecurity http, UserDetailsService userSvc) throws Exception {
		return http.getSharedObject(AuthenticationManagerBuilder.class)
			.userDetailsService(userSvc).passwordEncoder(passwordEncoder()).and().build();
	}
	
	@Bean
	public SecurityFilterChain jwtConfig(HttpSecurity http, JwtTokenFilter jwtFilter, JwtAuthenticationEntryPoint entryPoint) throws Exception {		
		String roles[] = List.of(AuthorityType.BUYER).stream().map(AuthorityType::getValue).collect(Collectors.toList()).toArray(new String[0]);
		return http.securityMatcher("/api/**").cors()
			.and()
				.csrf().disable().exceptionHandling().authenticationEntryPoint(entryPoint)
			.and()
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
				.authorizeHttpRequests(requests -> requests.requestMatchers(antMatcher(HttpMethod.GET, "/api/auth/me")).hasAnyAuthority(roles).anyRequest().permitAll())
		    		.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
	    		.build();
	}
}
