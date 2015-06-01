package com.growthbeat.http;

import java.net.URI;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

public class HttpEntityEnclosingRequest extends HttpEntityEnclosingRequestBase {

	private String method;

	public HttpEntityEnclosingRequest() {
		super();
	}

	public HttpEntityEnclosingRequest(URI uri) {
		super();
		setURI(uri);
	}

	public HttpEntityEnclosingRequest(String uri) {
		super();
		setURI(URI.create(uri));
	}

	@Override
	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

}
