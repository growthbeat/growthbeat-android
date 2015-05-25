package com.growthbeat.http;

public class HttpResponse {

	private int status;
	private String body;

	public HttpResponse() {
		super();
	}

	public HttpResponse(int status, String body) {
		this();
		setStatus(status);
		setBody(body);
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

}
