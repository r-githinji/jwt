package com.auth.example.jwt;

import java.security.Principal;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

public abstract class Api {
	
	@CrossOrigin(origins = {"localhost:3000"})
	@RestController
	@RequestMapping(path = "/api/auth", produces = {MediaType.APPLICATION_JSON_VALUE})
	public static class JwtLoginApi {
		
		private AuthenticationManager authMgr;
		private PasswordEncoder encoder;
		private UserService usrSvc;
		private JwtTokenUtil jwtUtil;
		@Autowired
		public JwtLoginApi(AuthenticationManager authMgr, PasswordEncoder encoder, UserService usrSvc, JwtTokenUtil jwtUtil) {
			this.authMgr = authMgr;
			this.encoder = encoder;
			this.usrSvc = usrSvc;
			this.jwtUtil = jwtUtil;
		}
		
		@GetMapping(path = "/signup")
		public ResponseEntity<User> create() {
			User login = new User();
			login.addAuth(AuthorityType.BUYER);
			return ResponseEntity.ok(login);
		}
		
		@PostMapping(path = "/signup")
		public ResponseEntity<?> create(@Valid @RequestBody Jwt.SignupRequest signup) throws Exceptions.UsernameNotAvailableException, Exceptions.PasswordsDoNotMatchException {			
			User login = signup.getLogin();
			if (!login.getPassword().contentEquals(signup.getConfirm())) {
				throw new Exceptions.PasswordsDoNotMatchException(Growl.of("error", "Passwords entered do not match!"));
			}
			try {
				usrSvc.findByName(login.getUsername());
				throw new Exceptions.UsernameNotAvailableException(Growl.of("error", "Username not available!"));
			} catch (UsernameNotFoundException unfe) {
				//proceed to create login
			}
			login.setPassword(encoder.encode(login.getPassword()));
			login.setEnabled(true);
			login = usrSvc.create(login);	
			return ResponseEntity.status(HttpStatus.CREATED).build();
		}
		
		@PostMapping(path = "/signin")
		public ResponseEntity<Jwt.Token> signin(@Valid @RequestBody Jwt.SigninRequest signin, HttpServletResponse response) {
			Authentication auth = authMgr.authenticate(new UsernamePasswordAuthenticationToken(signin.getUsername(), signin.getPassword()));
			UserDetails details = (UserDetails)auth.getPrincipal();
			Jwt.Token token = jwtUtil.issue(details);
			HttpHeaders headers = new HttpHeaders();
			headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, Boolean.TRUE.toString());
			headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, HttpHeaders.SET_COOKIE);
			Cookie cookie = new Cookie("refreshToken", token.refresh);
			response.addCookie(cookie);
			return ResponseEntity.ok().headers(headers).body(token);
		}
		
		@GetMapping(path = "/refresh")
		public ResponseEntity<Jwt.Token> refresh(@CookieValue String refreshToken) {
			Jwt.Token token = jwtUtil.renew(refreshToken);
			return ResponseEntity.ok(token);
		}
		
		@GetMapping(path = "/me")
		public ResponseEntity<User> self(Principal principal) {
			Authentication auth = (Authentication)principal;
			User login = (User)auth.getPrincipal();
			return ResponseEntity.ok(login);
		}
	}
}
