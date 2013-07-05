package com.vance.qualitycode;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.easymock.EasyMock;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class WebRetrieverTest {

    public static final String SCHEME_HTTP = "http";
    public static final String EXAMPLE_DOMAIN = "www.example.com";
    public static final String EXAMPLE_URI = SCHEME_HTTP + "://" + EXAMPLE_DOMAIN;

    @Test
    public void testWebRetriever() {
        WebRetriever sut = new WebRetriever();

        assertThat(sut, is(notNullValue()));
    }

    @Test
    public void testRetrieve_MultipleURIs() throws IOException, URISyntaxException {
        final String[] expectedContent = {
                "The first site's content",
                "The next site's content",
                "The last site's content"
        };

        WebRetriever sut = new WebRetriever() {
            int siteIndex = 0;

            @Override
            protected Target createTarget(String URI, boolean writeToFile) throws URISyntaxException {
                return new Target(URI, writeToFile) {
                    @Override
                    public void retrieve() throws IOException {
                        String content = expectedContent[siteIndex++];
                        setContent(content);
                    }
                };
            }
        };

        String[] sites = {"site1", "site2", "site3"};
        List<String> allContent = sut.retrieve(sites);

        assertArrayEquals(expectedContent, allContent.toArray());
    }

    @Test
    public void testRetrieve_SingleURLOutputToFile() throws IOException, URISyntaxException {
        final String expectedContent = "This content should go to a file";
        String[] args = {"-O", EXAMPLE_URI};
        WebRetriever sut = new WebRetriever() {
            int retrieveCount = 0;
            int emitCount = 0;

            @Override
            public List<String> retrieve(String[] URIs) throws IOException, URISyntaxException {
                List<String> result = super.retrieve(URIs);
                assertTrue(emitCount > 0);
                return result;
            }

            @Override
            protected Target createTarget(String URI, boolean writeToFile) throws URISyntaxException {
                return new Target(URI, writeToFile) {
                    @Override
                    public void retrieve() throws IOException, URISyntaxException {
                        assertThat(++retrieveCount, is(equalTo(1)));
                        super.retrieve();
                    }

                    @Override
                    protected void retrieveResponse() throws IOException {
                        setResponse(createMockResponse(expectedContent));
                    }

                    @Override
                    protected void emit() {
                        super.emit();
                        assertThat(getContent(), is(expectedContent));
                        assertThat(getOutputToFile(), is(true));
                        emitCount++;
                    }

                };
            }
        };

        List<String> content = sut.retrieve(args);

        assertThat(content.size(), is(1));
        assertThat(content.get(0), is(equalTo(expectedContent)));
    }

    protected static HttpResponse createMockResponse(String expectedContent) throws IOException {
        final HttpResponse response = EasyMock.createMock(HttpResponse.class);
        HttpEntity entity = EasyMock.createMock(HttpEntity.class);
        EasyMock.expect(response.getEntity()).andReturn(entity);
        EasyMock.expect(entity.getContent()).andReturn(new ByteArrayInputStream(expectedContent.getBytes()));
        EasyMock.replay(response, entity);
        return response;
    }
}
