package com.vance.qualitycode;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.easymock.EasyMock;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

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
    public void testRetrieve_SingleURI() throws IOException, URISyntaxException {
        final String expectedContent = "This is one set of content";
        WebRetriever sut = new WebRetriever() {
            @Override
            protected void retrieveResponse(Target target) throws IOException {
                target.setResponse(createMockResponse(expectedContent));
            }
        };

        String content = sut.retrieve(new WebRetriever.Target(EXAMPLE_URI));

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

        WebRetriever sut = new WebRetriever() {
            int siteIndex = 0;

            @Override
            public String retrieve(Target target) throws IOException {
                return expectedContent[siteIndex++];
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
            public String retrieve(Target target) throws IOException, URISyntaxException {
                assertThat(++retrieveCount, is(equalTo(1)));
                return super.retrieve(target);
            }

            @Override
            protected void retrieveResponse(Target target) throws IOException {
                target.setResponse(createMockResponse(expectedContent));
            }

            @Override
            protected void emit(String content, boolean writeToFile) {
                super.emit(content, writeToFile);
                assertThat(content, is(expectedContent));
                assertThat(writeToFile, is(true));
                emitCount++;
            }
        };

        List<String> content = sut.retrieve(args);

        assertThat(content.size(), is(1));
        assertThat(content.get(0), is(equalTo(expectedContent)));
    }

    @Test
    public void testWebRetrieverTarget_SchemeDomain() throws IOException, URISyntaxException {
        String expectedOriginal = EXAMPLE_URI;

        WebRetriever.Target sut = new WebRetriever.Target(expectedOriginal);

        assertThat(sut.getOriginal(), is(expectedOriginal));
        URI actualURI = sut.getUri();
        assertThat(actualURI.getScheme(), is(equalTo(SCHEME_HTTP)));
        assertThat(actualURI.getHost(), is(equalTo(EXAMPLE_DOMAIN)));
    }

    @Test
    public void testWebRetrieverTarget_DomainOnly() throws IOException, URISyntaxException {
        String expectedOriginal = EXAMPLE_DOMAIN;

        WebRetriever.Target sut = new WebRetriever.Target(expectedOriginal);

        assertThat(sut.getOriginal(), is(expectedOriginal));
        URI actualURI = sut.getUri();
        assertThat(actualURI.getHost(), is(equalTo(expectedOriginal)));
        assertThat(actualURI.getScheme(), is(equalTo(SCHEME_HTTP)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWebRetrieverTarget_NonHttpScheme() throws IOException, URISyntaxException {
        new WebRetriever.Target("ftp://" + EXAMPLE_DOMAIN);
    }

    @Test
    public void testTargetExtractContentFromResponse() throws IOException, URISyntaxException {
        String expectedContent = "This is another set of content";
        WebRetriever.Target sut = new WebRetriever.Target(EXAMPLE_URI);
        sut.setResponse(createMockResponse(expectedContent));

        String content = sut.extractContentFromResponse();

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
