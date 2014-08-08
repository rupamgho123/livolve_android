package com.hackday.livolve;

public class Issue{
	private String id;
	private String value;
	private String status;
	private String userId;
	private String summary;

	public Issue(String id, String value, String status, String userId, String summary) {
		setId(id);
		setStatus(status);
		setUserId(userId);
		setValue(value);
		setSummary(summary);
	}

	private void setSummary(String summary) {
		this.summary = summary;
	}
	
	public String getSummary(){
		return summary;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}
