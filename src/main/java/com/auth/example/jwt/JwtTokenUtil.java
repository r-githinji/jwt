package com.auth.example.jwt;

import java.io.Serializable;
import java.io.IOException;
import java.util.UUID;
import java.util.Date;
import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.Optional;
import java.security.Key;

import javax.crypto.spec.SecretKeySpec;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.JwtException;

@Component
public class JwtTokenUtil implements Serializable {

	private static final long serialVersionUID = 294528230980L;
	@Value("${jwt.secret}")
	private String secret;
	@Value("${jwt.token.duration}")
	private int duration;
	@Value("${jwt.refresh.validity}")
	private int refresh;
	private Map<String, Jwt.Token> tokens;

	@PostConstruct
	public void onCreate() {
		tokens = new ConcurrentHashMap<>();
	}
	
	private String issue(String to) {
		Calendar now = Calendar.getInstance();
		Date issued = now.getTime();
		now.roll(Calendar.MINUTE, duration);
		Date expires = now.getTime();
		Key key = new SecretKeySpec(secret.getBytes(), SignatureAlgorithm.HS512.getJcaName());		
		return Jwts.builder().setSubject(to).setIssuedAt(issued).setExpiration(expires).signWith(key).compact();
	}
	
	public Jwt.Token issue(UserDetails user) {
		Jwt.Token token = new Jwt.Token((User)user, issue(user.getUsername()), duration * 60 * 1000);
		tokens.put(token.refresh, token);
		return token;
	}
	
	public Optional<Jwt.Token> findByToken(String token) {		
		return Optional.of(tokens.get(token));
	}
	
	public Jwt.Token renew(String id) {
		Jwt.Token token = findByToken(id).orElseThrow(null);
		token.value = issue(token.login.getUsername());
		return token;
	}
		
	public String getUsername(String token) {
		return resolveClaimFromToken(token, Claims::getSubject);
	}
	
	public Date getTokenExpirationDate(String token) {
		return resolveClaimFromToken(token, Claims::getExpiration);
	}
	
	public Jws<Claims> getClaimsFromToken(String token) {
		Key key = new SecretKeySpec(secret.getBytes(), SignatureAlgorithm.HS512.getJcaName());
		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
	}
	
	public <T> T resolveClaimFromToken(String token, Function<Claims, T> resolver) {
		Claims claims = getClaimsFromToken(token).getBody();
		return resolver.apply(claims);
	}
	
	public boolean isTokenExpired(String token) {
		return Calendar.getInstance().getTime().after(getTokenExpirationDate(token));
	}
	
	@PreDestroy
	public void onDestroy() {
		tokens.clear();
	}
}
