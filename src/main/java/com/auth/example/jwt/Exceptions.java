package com.auth.example.jwt;

import java.util.List;

import org.springframework.security.core.AuthenticationException;

public abstract class Exceptions {
	
	public static class UsernameNotAvailableException extends Exception {
		
		private static final long serialVersionUID = 28473842L;
		private Growl error;
		
		public UsernameNotAvailableException(Growl error) {
			this.error = error;
		}

		public Growl getError() {
			return error;
		}
	}
	
	public static class PasswordsDoNotMatchException extends Exception {
		
		private static final long serialVersionUID = 33637281L;
		private Growl error;
		
		public PasswordsDoNotMatchException(Growl error) {
			this.error = error;
		}

		public Growl getError() {
			return error;
		}
	}
}

