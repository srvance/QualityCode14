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
        List<String> contents = new ArrayList<String>(URIs.length);
        boolean writeToFile = false;

        for (String URI : URIs) {
            if ("-O".equals(URI)) {
                writeToFile = true;
                continue;
            }
            Target target = new Target(URI);
            String content = retrieve(target);
            contents.add(content);
            emit(content, writeToFile);
            writeToFile = false;
        }

        return contents;
    }

    public String retrieve(Target target) throws IOException, URISyntaxException {
        retrieveResponse(target);

        return extractContentFromResponse(target.getResponse());
    }

    protected void emit(String content, boolean writeToFile) {

    }

    protected void retrieveResponse(Target target) throws IOException, URISyntaxException {
        URI uri = target.getUri();
        HttpGet httpGet = new HttpGet(uri);
        HttpResponse response = httpClient.execute(httpGet);
        target.setResponse(response);
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

    static class Target {
        private final String original;
        private final URI uri;

        private HttpResponse response;

        Target(String original) throws URISyntaxException {
            this.original = original;
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
    }
}
