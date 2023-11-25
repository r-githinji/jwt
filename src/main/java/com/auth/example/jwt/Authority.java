package com.auth.example.jwt;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Cacheable(false)
@Entity
@Table(name = "auth")
public class Authority implements Serializable {

	private static final long serialVersionUID = 738483948304784589L;
	private Long id;
	private AuthorityType name;
	private User login;
		
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "auth_id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@NotNull
	@Column(name = "auth_name", nullable = false, unique = true, updatable = false)
	public AuthorityType getName() {
		return name;
	}
	public void setName(AuthorityType name) {
		this.name = name;
	}
	@JsonBackReference
	@NotNull
	@ManyToOne
	@JoinColumn(name = "auth_usr")
	public User getLogin() {
		return login;
	}
	public void setLogin(User login) {
		this.login = login;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(name, login);
	}
		
	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		} else if (null == other || !(other instanceof Authority)) {
			return false;
		} 
		Authority that = (Authority)other;
		return name == that.getName() && login.equals(that.getLogin());
	}
}
