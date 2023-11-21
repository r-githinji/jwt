package com.auth.example.jwt;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.ParameterMode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

@Repository
public class UserRepository {

	private EntityManager em;
	@Autowired
	public UserRepository(EntityManager em) {
		this.em = em;
	}
	
	public User create(User login) {
		return em.merge(login);
	}
	
	public User findById(Integer id) {
		return em.find(User.class, id);
	}
	
	public User findByName(String name) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<User> cq = cb.createQuery(User.class);
		Root<User> tbl = cq.from(User.class);
		Predicate[] preds = new Predicate[] {cb.equal(tbl.get("username"), name)};
		cq = cq.select(tbl).where(preds);
		return em.createQuery(cq).getResultList().stream().findFirst().orElse(null);
	}

	public int count() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<User> usr = cq.from(User.class);
		cq = cq.select(cb.count(usr)).where();
		return em.createQuery(cq).getSingleResult().intValue();
	}

	public List<User> list(int offset, int limit) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<User> cq = cb.createQuery(User.class);
		Root<User> usr = cq.from(User.class);
		cq = cq.select(usr).where();
		return em.createQuery(cq).setFirstResult(offset).setMaxResults(limit).getResultList();	
	}
	
	public boolean delete(User user) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaDelete<User> cd = cb.createCriteriaDelete(User.class);
		Root<User> usr = cd.from(User.class);
		Predicate[] preds = new Predicate[] {cb.equal(usr.get("id"), user.getId())};
		cd = cd.where(preds);		
		return em.createQuery(cd).executeUpdate() > 0;
	}
	
	public boolean update(User user, Map<String, ?> params) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaUpdate<User> cu = cb.createCriteriaUpdate(User.class);
		Root<User> usr = cu.from(User.class);
		for (Map.Entry<String, ?> tuple: params.entrySet()) {
			cu = cu.set(tuple.getKey(), tuple.getValue());
		}
		Predicate[] preds = new Predicate[] {cb.equal(usr.get("id"), user.getId())};
		cu = cu.where(preds);
		return em.createQuery(cu).executeUpdate() > 0;
	}
}
