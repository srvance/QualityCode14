package com.vance.qualitycode;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class WebRetriever {

    private final HttpClient httpClient;

    public WebRetriever() {
        this(new DefaultHttpClient());
    }

    private WebRetriever(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public static void main(String[] args) {
        WebRetriever retriever = new WebRetriever();
        try {
            System.out.println(retriever.retrieve(args));
        } catch (IOException e) {
            System.err.println("Houston, we have a problem.");
        }
    }

    public String retrieve(String URI) throws IOException {
        HttpResponse response = retrieveResponse(URI);

        return extractContentFromResponse(response);
    }

    protected HttpResponse retrieveResponse(String URI) throws IOException {
        HttpGet httpGet = new HttpGet(URI);
        return httpClient.execute(httpGet);
    }

    protected String extractContentFromResponse(HttpResponse response) throws IOException {
        HttpEntity entity = response.getEntity();
        InputStream content = entity.getContent();
        StringWriter writer = new StringWriter();
        IOUtils.copy(content, writer);
        return writer.toString();
    }

    public String retrieve(String[] URIs) throws IOException {
        List<String> content = new ArrayList<String>(URIs.length);

        for (String URI : URIs) {
            content.add(retrieve(URI));
        }

        return StringUtils.join(content, '\n');
    }
}
