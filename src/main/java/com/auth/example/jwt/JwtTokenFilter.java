package com.auth.example.jwt;

import java.io.IOException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.FilterChain;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;

import io.jsonwebtoken.JwtException;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

	private UserDetailsService userSvc;	
	private JwtTokenUtil jwtUtil;
	@Autowired
	public JwtTokenFilter(UserDetailsService userSvc, JwtTokenUtil jwtUtil) {
		this.userSvc = userSvc;
		this.jwtUtil = jwtUtil;
	}
	
	@PostConstruct
	public void onCreate() {
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) 
		throws ServletException, IOException {
		String auth = request.getHeader(HttpHeaders.AUTHORIZATION), username = null;
		if (null != auth && !auth.isEmpty() && auth.startsWith("Bearer ")) {
			String token = auth.substring(7);
			try {
				username = jwtUtil.getUsername(token);	
			} catch (JwtException je) {
				//handle jwt exceptions
			} 
		}
		SecurityContext ctx = SecurityContextHolder.getContext();
		if (username != null && null == ctx.getAuthentication()) {
			UserDetails user = userSvc.loadUserByUsername(username);
			UsernamePasswordAuthenticationToken upat = new UsernamePasswordAuthenticationToken(user, 
				null, user.getAuthorities());
			WebAuthenticationDetailsSource wads = new WebAuthenticationDetailsSource();
			upat.setDetails(wads.buildDetails(request));
			ctx.setAuthentication(upat);
		}
		chain.doFilter(request, response);
	}	
	
	@PreDestroy
	public void onDestroy() {
	}
}
