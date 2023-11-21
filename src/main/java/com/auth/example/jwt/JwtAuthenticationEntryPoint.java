package com.auth.example.jwt;

import java.io.IOException;
import java.io.Serializable;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class JwtAuthenticationEntryPoint implements Serializable, AuthenticationEntryPoint {

	private static final long serialVersionUID = 48223672300L;
	private static Logger logger = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);
	private ObjectMapper mapper;
	@Autowired
	public JwtAuthenticationEntryPoint(ObjectMapper mapper) {
		this.mapper = mapper;
	}

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException aex) 
		throws IOException, ServletException {
		String content;
		if (aex instanceof InsufficientAuthenticationException) {
			//unauthorized
		} else if (aex instanceof DisabledException) {
			//disabled
		} else if (aex instanceof BadCredentialsException) {
			//bad credentials
		} else {
			//default exception handler
		}
		Growl growl = Growl.of("error", aex.getMessage());
		try {
			content = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(growl);
		} catch (JsonProcessingException jpex) {
			throw new ServletException(jpex);
		}
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentLength(content.getBytes().length);
		PrintWriter writer = response.getWriter();
		writer.print(content);
			
		logger.info("reason = {}, type = {}, uri = {}", content, aex.getClass().getName(), request.getRequestURI());	
	}
}
