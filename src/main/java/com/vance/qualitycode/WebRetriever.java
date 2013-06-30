package com.vance.qualitycode;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

public class WebRetriever {
    public static void main(String[] args) {
    }

    public String retrieve(String URI) throws IOException {
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(URI);
        HttpResponse response = httpClient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        InputStream content = entity.getContent();
        StringWriter writer = new StringWriter();
        IOUtils.copy(content, writer, entity.getContentEncoding().getValue());
        return writer.toString();
    }
}
