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
        Target currentTarget;

        for (String URI : URIs) {
            if ("-O".equals(URI)) {
                writeToFile = true;
                continue;
            }
            currentTarget = new Target(URI, writeToFile);
            retrieve(currentTarget);
            contents.add(currentTarget.getContent());
            emit(currentTarget);
            writeToFile = false;
        }

        return contents;
    }

    public void retrieve(Target target) throws IOException, URISyntaxException {
        retrieveResponse(target);

        target.extractContentFromResponse();
    }

    protected void emit(Target target) {

    }

    protected void retrieveResponse(Target target) throws IOException, URISyntaxException {
        URI uri = target.getUri();
        HttpGet httpGet = new HttpGet(uri);
        HttpResponse response = httpClient.execute(httpGet);
        target.setResponse(response);
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
}
