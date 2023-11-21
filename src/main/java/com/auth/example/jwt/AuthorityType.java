package com.auth.example.jwt;

public enum AuthorityType {

	ADMIN("Admin", "auth.ADMIN"), 
	BUYER("Buyer", "auth.BUYER"), 
	SELLER("Seller", "auth.SELLER");
	private final String label, value;

	private AuthorityType(String label, String name) {
		this.label = label;
		this.value = name;
	}
	public String getLabel() {
		return label;
	}
	public String getValue() {
		return value;
	}
}
