package com.auth.example.jwt;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional @Service
public class UserService implements UserDetailsService {

	private UserRepository userRepo;
	private transient Map<String, User> cache;
	@Autowired
	public UserService(UserRepository userRepo) {
		this.userRepo = userRepo;
	}
	
	@PostConstruct
	public void onCreate() {
		cache = new ConcurrentHashMap<>();
	}
	
	public User create(AuthorityType ...roles) {
		User user = new User();
		List.of(roles).forEach(user::addAuth);
		return user;
	}
	
	public User findByName(String username) {
		User user = cache.get(username);
		if (user != null) {
			return user;
		}		
		user = userRepo.findByName(username);
		if (null == user) {
			throw new UsernameNotFoundException(String.format("User %s not found!", username));
		}
		cache.put(user.getUsername(), user);
		return user;
	}
	
	public User findById(Integer id) {
		return userRepo.findById(id);
	}

	public User create(User user) {
		return userRepo.create(user);
	}

	public boolean delete(User user) {
		return userRepo.delete(user);
	}

	public boolean update(User user, Map<String, ?> params) {
		boolean success = userRepo.update(user, params);
		cache.remove(user.getUsername());
		return success;
	}

	public int count() {
		return userRepo.count();
	}

	public List<User> list(int offset, int limit) {
		return userRepo.list(offset, limit);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return findByName(username);
	}
	
	@PreDestroy
	public void onDestroy() {
		cache.clear();		
	}
}
