package com.vance.qualitycode;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;

class Target {
    private final String original;
    private final URI uri;
    private final boolean outputToFile;

    private HttpResponse response;

    private String content;

    Target(String original, boolean outputToFile) throws URISyntaxException {
        this.original = original;
        this.outputToFile = outputToFile;
        uri = rectifyURI(original);
    }

    private URI rectifyURI(String URI) throws URISyntaxException {
        URI uri = new URI(URI);
        if (uri.getHost() == null) {
            uri = new URI("http://" + URI);
        }
        if (!isSupportedScheme(uri.getScheme())) {
            throw new IllegalArgumentException("Only http scheme is valid at this time");
        }
        return uri;
    }

    private boolean isSupportedScheme(String scheme) {
        return "http".equals(scheme);
    }

    public String getOriginal() {
        return original;
    }

    protected URI getUri() {
        return uri;
    }

    HttpResponse getResponse() {
        return response;
    }

    void setResponse(HttpResponse response) {
        this.response = response;
    }

    protected String extractContentFromResponse() throws IOException {
        HttpResponse response = getResponse();
        HttpEntity entity = response.getEntity();
        InputStream content = entity.getContent();
        StringWriter writer = new StringWriter();
        IOUtils.copy(content, writer);
        this.content = writer.toString();
        return this.content;
    }

    protected boolean getOutputToFile() {
        return outputToFile;
    }

    protected String getContent() {
        return content;
    }

    protected void setContent(String content) {
        this.content = content;
    }
}
