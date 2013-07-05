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

    public List<String> retrieve(String[] URIs) throws IOException, URISyntaxException {
        List<String> content = new ArrayList<String>(URIs.length);
        boolean writeToFile = false;

        for (String URI : URIs) {
            if ("-O".equals(URI)) {
                writeToFile = true;
                continue;
            }
            content.add(retrieve(URI, writeToFile));
            writeToFile = false;
        }

        return content;
    }

    public String retrieve(String URI, boolean writeToFile) throws IOException, URISyntaxException {
        HttpResponse response = retrieveResponse(URI);

        return extractContentFromResponse(response);
    }

    protected HttpResponse retrieveResponse(String URI) throws IOException, URISyntaxException {
        URI uri = rectifyURI(URI);
        return retrieveResponse(uri);
    }

    protected HttpResponse retrieveResponse(URI uri) throws IOException {
        HttpGet httpGet = new HttpGet(uri);
        return httpClient.execute(httpGet);
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

    protected String extractContentFromResponse(HttpResponse response) throws IOException {
        HttpEntity entity = response.getEntity();
        InputStream content = entity.getContent();
        StringWriter writer = new StringWriter();
        IOUtils.copy(content, writer);
        return writer.toString();
    }

    public static void main(String[] args) {
        WebRetriever retriever = new WebRetriever();
        try {
            List<String> contents = retriever.retrieve(args);
            System.out.println(StringUtils.join(contents, '\n'));
        } catch (IOException e) {
            System.err.println("Houston, we have a problem.");
            e.printStackTrace();
        } catch (URISyntaxException e) {
            System.err.println("Bad URL!");
            e.printStackTrace();
        }
    }
}
