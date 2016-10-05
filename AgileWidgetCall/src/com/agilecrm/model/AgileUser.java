package com.agilecrm.model;

import com.google.gson.JsonObject;

public class AgileUser {

	private String id;
	private String name = null;
	private String domain;
	private String email;
	private JsonObject json;



	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "AgileUser [id=" + id + ", domain=" + domain + ", email="
				+ email + ", name=" + name
				+ "]";
	}


	public JsonObject getJson() {
		return json;
	}

	public void setJson(JsonObject json) {
		this.json = json;
	}
	
	
	

}
