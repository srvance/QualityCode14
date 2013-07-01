package com.vance.qualitycode;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.easymock.EasyMock;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class WebRetrieverTest {

    @Test
    public void testWebRetriever() {
        WebRetriever sut = new WebRetriever();

        assertThat(sut, is(notNullValue()));
    }

    @Test
    public void testRetrieve_SingleURI() throws IOException, URISyntaxException {
        final String expectedContent = "This is one set of content";
        WebRetriever sut = new WebRetriever() {
            @Override
            protected HttpResponse retrieveResponse(String URI) throws IOException {
                return createMockResponse(expectedContent);
            }
        };

        String content = sut.retrieve("http://www.example.com");

        assertThat(content, is(notNullValue()));
        assertThat(content, is(equalTo(expectedContent)));
    }

    @Test
    public void testRetrieve_MultipleURIs() throws IOException, URISyntaxException {
        final String[] expectedContent = {
                "The first site's content",
                "The next site's content",
                "The last site's content"
        };
        String allExpectedContent = StringUtils.join(expectedContent, '\n');

        WebRetriever sut = new WebRetriever() {
            int siteIndex = 0;

            @Override
            public String retrieve(String URI) throws IOException {
                return expectedContent[siteIndex++];
            }
        };

        String[] sites = {"site1", "site2", "site3"};
        String allContent = sut.retrieve(sites);

        assertThat(allContent, is(equalTo(allExpectedContent)));
    }

    @Test
    public void testRetrieveResponse_DomainOnly() throws IOException, URISyntaxException {
        WebRetriever sut = new WebRetriever();

        HttpResponse response = sut.retrieveResponse("www.example.com");

        assertThat(response, is(notNullValue()));
    }

    @Test
    public void testExtractContentFromResponse() throws IOException {
        String expectedContent = "This is another set of content";
        WebRetriever sut = new WebRetriever();

        String content = sut.extractContentFromResponse(createMockResponse(expectedContent));

        assertThat(content, is(equalTo(expectedContent)));
    }

    private HttpResponse createMockResponse(String expectedContent) throws IOException {
        final HttpResponse response = EasyMock.createMock(HttpResponse.class);
        HttpEntity entity = EasyMock.createMock(HttpEntity.class);
        EasyMock.expect(response.getEntity()).andReturn(entity);
        EasyMock.expect(entity.getContent()).andReturn(new ByteArrayInputStream(expectedContent.getBytes()));
        EasyMock.replay(response, entity);
        return response;
    }
}
