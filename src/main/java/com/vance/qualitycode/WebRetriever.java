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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class WebRetriever {

    private final HttpClient httpClient;

    public WebRetriever() {
        this.httpClient = new DefaultHttpClient();
    }

    public String retrieve(String URI) throws IOException, URISyntaxException {
        HttpResponse response = retrieveResponse(URI);

        return extractContentFromResponse(response);
    }

    protected HttpResponse retrieveResponse(String URI) throws IOException, URISyntaxException {
        URI uri = rectifyURI(URI);
        return retrieveResponse(uri);
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

    protected HttpResponse retrieveResponse(URI uri) throws IOException {
        HttpGet httpGet = new HttpGet(uri);
        return httpClient.execute(httpGet);
    }

    protected String extractContentFromResponse(HttpResponse response) throws IOException {
        HttpEntity entity = response.getEntity();
        InputStream content = entity.getContent();
        StringWriter writer = new StringWriter();
        IOUtils.copy(content, writer);
        return writer.toString();
    }

    public String retrieve(String[] URIs) throws IOException, URISyntaxException {
        List<String> content = new ArrayList<String>(URIs.length);

        for (String URI : URIs) {
            content.add(retrieve(URI));
        }

        return StringUtils.join(content, '\n');
    }

    public static void main(String[] args) {
        WebRetriever retriever = new WebRetriever();
        try {
            System.out.println(retriever.retrieve(args));
        } catch (IOException e) {
            System.err.println("Houston, we have a problem.");
            e.printStackTrace();
        } catch (URISyntaxException e) {
            System.err.println("Bad URL!");
            e.printStackTrace();
        }
    }
}
