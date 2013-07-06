package com.vance.qualitycode;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;

class Target {
    public static final String SCHEME_HTTP = "http";

    private final String original;
    private final URI uri;
    private final boolean outputToFile;
    private final HttpClient httpClient;

    private HttpResponse response;

    Target(String original, boolean outputToFile) throws URISyntaxException {
        this.original = original;
        this.outputToFile = outputToFile;
        this.httpClient = new DefaultHttpClient();

        uri = rectifyURI(original);
    }

    public void retrieve() throws IOException, URISyntaxException {
        retrieveResponse();

        extractContentFromResponse();
    }

    private URI rectifyURI(String URI) throws URISyntaxException {
        URI uri = new URI(URI);
        if (uri.getHost() == null) {
            uri = new URI(SCHEME_HTTP + "://" + URI);
        }
        if (!isSupportedScheme(uri.getScheme())) {
            throw new IllegalArgumentException("Only http scheme is valid at this time");
        }
        return uri;
    }

    private boolean isSupportedScheme(String scheme) {
        return SCHEME_HTTP.equals(scheme);
    }

    protected void retrieveResponse() throws IOException, URISyntaxException {
        HttpGet httpGet = new HttpGet(uri);
        response = httpClient.execute(httpGet);
    }

    protected void extractContentFromResponse() throws IOException {
        InputStream content = extractContentInputStream();
        OutputStream output = determineOutputStream();
        copyToOutput(content, output);
    }

    private InputStream extractContentInputStream() throws IOException {
        HttpEntity entity = response.getEntity();
        return entity.getContent();
    }

    protected void copyToOutput(InputStream content, OutputStream output) throws IOException {
        IOUtils.copy(content, output);
    }

    protected OutputStream determineOutputStream() {
        return System.out;
    }

    protected void emit() {

    }

    public String getOriginal() {
        return original;
    }

    protected URI getUri() {
        return uri;
    }

    void setResponse(HttpResponse response) {
        this.response = response;
    }

    protected boolean getOutputToFile() {
        return outputToFile;
    }
}
