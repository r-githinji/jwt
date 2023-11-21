package com.auth.example.jwt;

import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class Jwt {

	public static class SigninRequest {
		
		private String username, password;
		
		@NotEmpty(message = "{jwt.username.isempty}")
		public String getUsername() {
			return username;
		}
		public void setUsername(String username) {
			this.username = username;
		}
		@NotEmpty(message = "{jwt.password.isempty}")
		public String getPassword() {
			return password;
		}
		public void setPassword(String password) {
			this.password = password;
		}
	}
	
	public static class Token {
		
		public String value;
		public int expires;
		@JsonIgnore	
		public String refresh;
		@JsonIgnore
		public User login; 

		public Token(User login, String value, int expires) {
			this.login = login;
			this.value = value;
			this.expires = expires;
			//use appropriate mechanism to generate a unique refresh token key
			this.refresh = UUID.randomUUID().toString();
		}
	}
	
	public static class SignupRequest {	
		
		private String confirm;		
		private User login;
		
		@NotEmpty(message = "{confirm.password.isempty}")
		@Pattern(regexp = Constants.PWD_REGEX, message = "{login.password.pattern}")
		public String getConfirm() {
			return confirm;
		}		
		public void setConfirm(String confirm) {
			this.confirm = confirm;
		}	
		@Valid @NotNull	
		public User getLogin() {
			return login;
		}		
		public void setLogin(User login) {
			this.login = login;
		}
	}
}
