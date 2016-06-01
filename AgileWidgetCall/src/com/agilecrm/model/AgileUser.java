package com.agilecrm.model;

import org.json.JSONObject;

public class AgileUser {

	private Long id;
	private String name = null;
	private String domain;
	private String email;
	private JSONObject json;



	public Long getId() {
		return id;
	}

	public void setId(Long id) {
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


	public JSONObject getJson() {
		return json;
	}

	public void setJson(JSONObject json) {
		this.json = json;
	}
	
	
	

}
