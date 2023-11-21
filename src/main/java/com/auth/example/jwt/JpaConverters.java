package com.auth.example.jwt;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Stream;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

public abstract class JpaConverters {
	
	@Converter(autoApply = true)
	public static class AuthorityTypeJPAConverter implements AttributeConverter<AuthorityType, String> {

		@Override
		public String convertToDatabaseColumn(AuthorityType auth) {
			return auth.getValue();
		}

		@Override
		public AuthorityType convertToEntityAttribute(String dbData) {
			return Stream.of(AuthorityType.values())
				.filter(auth -> auth.getValue().contentEquals(dbData))
					.findFirst().orElseThrow(IllegalArgumentException::new);
		}
	}
}
