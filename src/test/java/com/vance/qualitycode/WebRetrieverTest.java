package com.vance.qualitycode;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.easymock.EasyMock;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class WebRetrieverTest {
    @Test
    public void testWebRetriever() {
        WebRetriever sut = new WebRetriever();

        assertThat(sut, notNullValue());
    }

    @Test
    public void testRetrieve_SingleURI() throws IOException {

        final HttpResponse response = EasyMock.createMock(HttpResponse.class);
        HttpEntity entity = EasyMock.createMock(HttpEntity.class);
        EasyMock.expect(response.getEntity()).andReturn(entity);
        EasyMock.replay(response, entity);
        WebRetriever sut = new WebRetriever() {
            @Override
            protected HttpResponse retrieveResponse(String URI) throws IOException {
                return response;
            }
        };

        String content = sut.retrieve("http://www.example.com");

        assertThat(content, notNullValue());
        assertThat(content, containsString("<html>"));
        assertThat(content, containsString("<title>Example Domain</title>"));
    }
}
