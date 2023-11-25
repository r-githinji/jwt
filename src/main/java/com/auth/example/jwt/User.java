package com.auth.example.jwt;

import java.io.Serializable;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.List;
import java.util.LinkedList;
import java.util.Collection;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Transient;
import jakarta.persistence.Cacheable;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.Valid;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Cacheable(false)
@Entity
@Table(name = "usr")
public class User implements Serializable, UserDetails {

	private static final long serialVersionUID = 529436539923782L;
	private Integer id;
	private String username;
	private String password;
	private LocalDateTime created;
	private boolean enabled;
	private List<Authority> roles;
    
	public User() {
		this.created = LocalDateTime.now();
	}

	@Id
	@Column(name = "usr_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	@NotEmpty(message = "{login.username.isempty}")
	@Column(name = "usr_name", nullable = false, unique = true, updatable = false)
	@Override
	public String getUsername() {
		return username;
	}
	public void setUsername(String name) {
		this.username = name;
	}
	@NotEmpty(message = "{login.password.isempty}")
	@Column(name = "usr_pwd", nullable = false, updatable = false)
	@Override
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	@NotNull
	@Column(name = "usr_created", nullable = false, updatable = false)
	public LocalDateTime getCreated() {
		return created;
	}
	public void setCreated(LocalDateTime createDate) {
		this.created = createDate;
	}
	@Column(name = "usr_enabled", nullable = false)
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	@Size(min = 1)
	@JsonManagedReference
	@OneToMany(mappedBy = "login", cascade = {CascadeType.ALL})
	public List<Authority> getRoles() {
		return roles;
	}
	public void setRoles(List<Authority> authorities) {
		this.roles = authorities;
	}

	public void clearAuths() {
		if (null != roles) {
			roles.clear();
		}
	}

	public void addAuth(AuthorityType auth) {
		if (null == auth) {
			return;
		}
		if (null == roles) {
			roles = new LinkedList<>();
		}
		if (null == roles.stream().filter(a -> a.getName() == auth).findFirst().orElse(null)) {
			Authority authority = new Authority();
			authority.setLogin(this);
			authority.setName(auth);
			roles.add(authority);
		}
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(username);
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		} else if (null == other || !(other instanceof User)) {
			return false;
		}  
		User that = (User)other;
		return username.contentEquals(that.getUsername());
	}
    
	@JsonIgnore
	@Transient
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return null == getRoles() ? List.of() : getRoles().stream().map((role) -> new SimpleGrantedAuthority(role.getName().getValue())).collect(Collectors.toList());
	}

	@JsonIgnore
	@Transient
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@JsonIgnore
	@Transient
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@JsonIgnore
	@Transient
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}
}
