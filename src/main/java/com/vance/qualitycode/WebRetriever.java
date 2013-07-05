package com.vance.qualitycode;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
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

}
