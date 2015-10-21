package com.growthbeat.http;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

import android.os.Build;

import com.growthbeat.GrowthbeatException;

public class BaseHttpClient {

    private static final String CONTENT_CHARSET = "UTF-8";

    public enum RequestMethod {
        GET, POST, PUT, DELETE
    }

    private String baseUrl = null;
    private int connectTimeout = 60 * 1000;
    private int readTimeout = 60 * 1000;

    public BaseHttpClient() {
        super();
    }

    public BaseHttpClient(String baseUrl, int connectTimeout, int readTimeout) {
        this();
        setBaseUrl(baseUrl);
        setConnectTimeout(connectTimeout);
        setReadTimeout(readTimeout);
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public String request(RequestMethod requestMethod, String path, Map<String, Object> parameters) {

        String query = (requestMethod == RequestMethod.GET) ? "?" : "";
        for (Entry<String, Object> parameter : parameters.entrySet()) {
            try {
                query += parameter.getKey() + "=" + URLEncoder.encode((String) parameter.getValue(), CONTENT_CHARSET) + "&";
            } catch (UnsupportedEncodingException e) {
            }
        }
        query = query.substring(0, query.length() - 1);

        String url = String.format("%s%s", baseUrl, path);

        return request(requestMethod, url, query);

    }

    protected String request(RequestMethod requestMethod, String urlString, String query) {

        String response = null;
        HttpURLConnection httpURLConnection = generateHttpURLConnection(requestMethod, urlString, query);
        InputStream inputStream = null;

        try {
            httpURLConnection.connect();

            if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new GrowthbeatException(httpURLConnection.getResponseMessage());
            } else {
                inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
                String line = "";
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, CONTENT_CHARSET));
                StringBuilder stringBuilder = new StringBuilder();

                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                bufferedReader.close();
                response = stringBuilder.toString();
            }
        } catch (IOException e) {
            throw new GrowthbeatException("Failed to connection. " + e.getMessage(), e);
        } finally {
            if (inputStream != null)
                try {
                    inputStream.close();
                } catch (IOException e) {
                    throw new GrowthbeatException("Failed to close connection. " + e.getMessage(), e);
                }
            httpURLConnection.disconnect();
        }

        return response;

    }

    private HttpURLConnection generateHttpURLConnection(RequestMethod requestMethod, String urlString, String query) {

        try {

            if (requestMethod == RequestMethod.GET && query != null && query.length() > 0)
                urlString += query;

            URL url = new URL(urlString);

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
                System.setProperty("http.keepAlive", "false");
            }

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB_MR2 && Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                httpURLConnection.setRequestProperty("Connection", "close");
            }

            httpURLConnection.setConnectTimeout(getConnectTimeout());
            httpURLConnection.setReadTimeout(getReadTimeout());
            httpURLConnection.setRequestMethod(requestMethod.toString());
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=" + CONTENT_CHARSET);
            httpURLConnection.setRequestProperty("Accept", "application/json");

            if (requestMethod != RequestMethod.GET) {
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                if (query != null) {
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    outputStream.write(query.getBytes(CONTENT_CHARSET));
                    outputStream.flush();
                    outputStream.close();
                }
            }

            return httpURLConnection;

        } catch (MalformedURLException e) {
        } catch (IOException e) {
        }

        throw new GrowthbeatException("Failed create HttpURLConnection");

    }

}
