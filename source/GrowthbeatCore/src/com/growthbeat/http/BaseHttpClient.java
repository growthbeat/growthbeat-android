package com.growthbeat.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import com.growthbeat.GrowthbeatException;
import com.growthbeat.utils.HttpUtils;
import com.growthbeat.utils.IOUtils;

public class BaseHttpClient {

	private HttpClient httpClient = null;
	private String baseUrl = null;

	public BaseHttpClient() {
		super();
		HttpParams httpParams = new BasicHttpParams();
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
		this.httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(httpParams, schemeRegistry), httpParams);
	}

	public BaseHttpClient(String baseUrl, int connectionTimeout, int socketTimeout) {
		this();
		setBaseUrl(baseUrl);
		setConnectionTimeout(connectionTimeout);
		setSocketTimeout(socketTimeout);
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public int getConnectionTimeout() {
		return HttpConnectionParams.getConnectionTimeout(httpClient.getParams());
	}

	public void setConnectionTimeout(int timeout) {
		HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), timeout);
	}

	public int getSocketTimeout() {
		return HttpConnectionParams.getSoTimeout(httpClient.getParams());
	}

	public void setSocketTimeout(int timeout) {
		HttpConnectionParams.setSoTimeout(httpClient.getParams(), timeout);
	}

	public HttpResponse request(HttpRequest httpRequest) {

		String query = URLEncodedUtils.format(HttpUtils.makeNameValuePairs(httpRequest.getParameters()), "UTF-8");
		String url = String.format("%s%s", baseUrl, httpRequest.getPath());

		HttpUriRequest httpUriRequest = null;
		if (httpRequest.getMethod() != null && httpRequest.getMethod().equalsIgnoreCase("GET")) {
			url = url + (query.length() == 0 ? "" : "?" + query);
			httpUriRequest = new HttpGet(url);
		} else {
			HttpEntityEnclosingRequest httpEntityEnclosingRequest = new HttpEntityEnclosingRequest(url);
			httpEntityEnclosingRequest.setMethod(httpRequest.getMethod());
			httpEntityEnclosingRequest.setEntity(httpRequest.getEntity());
			httpUriRequest = httpEntityEnclosingRequest;
		}

		for (Map.Entry<String, String> entry : httpRequest.getHeaders().entrySet())
			httpUriRequest.setHeader(entry.getKey(), entry.getValue());

		return request(httpUriRequest);

	}

	protected HttpResponse request(HttpUriRequest httpUriRequest) {

		org.apache.http.HttpResponse httpResponse = null;
		try {
			httpResponse = httpClient.execute(httpUriRequest);
		} catch (IOException e) {
			throw new GrowthbeatException("Feiled to execute HTTP request. " + e.getMessage(), e);
		}

		String body = null;
		try {
			InputStream inputStream = httpResponse.getEntity().getContent();
			body = IOUtils.toString(inputStream);
		} catch (IOException e) {
			throw new GrowthbeatException("Failed to read HTTP response. " + e.getMessage(), e);
		} finally {
			try {
				httpResponse.getEntity().consumeContent();
			} catch (IOException e) {
				throw new GrowthbeatException("Failed to close connection. " + e.getMessage(), e);
			}
		}

		return new HttpResponse(httpResponse.getStatusLine().getStatusCode(), body);

	}

}
