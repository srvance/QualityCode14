package com.vance.qualitycode;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.easymock.EasyMock;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

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
            protected HttpResponse retrieveResponse(String URI) throws IOException {
                return createMockResponse(expectedContent);
            }
        };

        String content = sut.retrieve(EXAMPLE_URI);

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
    public void testRetrieveResponse_SchemeDomain() throws IOException, URISyntaxException {
        WebRetrieverURISpy sut = new WebRetrieverURISpy();

        sut.retrieveResponse(EXAMPLE_URI);

        URI actualURI = sut.getSuppliedURI();
        assertThat(actualURI.getScheme(), is(equalTo(SCHEME_HTTP)));
        assertThat(actualURI.getHost(), is(equalTo(EXAMPLE_DOMAIN)));
    }

    @Test
    public void testRetrieveResponse_DomainOnly() throws IOException, URISyntaxException {
        WebRetrieverURISpy sut = new WebRetrieverURISpy();

        sut.retrieveResponse(EXAMPLE_DOMAIN);

        URI actualURI = sut.getSuppliedURI();
        assertThat(actualURI.getHost(), is(equalTo(EXAMPLE_DOMAIN)));
        assertThat(actualURI.getScheme(), is(equalTo(SCHEME_HTTP)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRetrieveResponse_NonHttpScheme() throws IOException, URISyntaxException {
        WebRetrieverURISpy sut = new WebRetrieverURISpy();

        sut.retrieveResponse("ftp://" + EXAMPLE_DOMAIN);
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

    private class WebRetrieverURISpy extends WebRetriever {
        URI suppliedURI;

        @Override
        protected HttpResponse retrieveResponse(URI uri) throws IOException {
            this.suppliedURI = uri;
            return createMockResponse("");
        }

        public URI getSuppliedURI() {
            return suppliedURI;
        }
    }
}
