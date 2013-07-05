package com.vance.qualitycode;

import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class TargetTest {
    @Test
    public void testWebRetrieverTarget_SchemeDomain() throws IOException, URISyntaxException {
        String expectedOriginal = WebRetrieverTest.EXAMPLE_URI;

        Target sut = new Target(expectedOriginal, false);

        assertThat(sut.getOriginal(), is(expectedOriginal));
        assertThat(sut.getOutputToFile(), is(false));
        URI actualURI = sut.getUri();
        assertThat(actualURI.getScheme(), is(equalTo(WebRetrieverTest.SCHEME_HTTP)));
        assertThat(actualURI.getHost(), is(equalTo(WebRetrieverTest.EXAMPLE_DOMAIN)));
    }

    @Test
    public void testWebRetrieverTarget_DomainOnly() throws IOException, URISyntaxException {
        String expectedOriginal = WebRetrieverTest.EXAMPLE_DOMAIN;

        Target sut = new Target(expectedOriginal, false);

        assertThat(sut.getOriginal(), is(expectedOriginal));
        assertThat(sut.getOutputToFile(), is(false));
        URI actualURI = sut.getUri();
        assertThat(actualURI.getHost(), is(equalTo(expectedOriginal)));
        assertThat(actualURI.getScheme(), is(equalTo(WebRetrieverTest.SCHEME_HTTP)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWebRetrieverTarget_NonHttpScheme() throws IOException, URISyntaxException {
        new Target("ftp://" + WebRetrieverTest.EXAMPLE_DOMAIN, false);
    }

    @Test
    public void testTargetExtractContentFromResponse() throws IOException, URISyntaxException {
        String expectedContent = "This is another set of content";
        Target sut = new Target(WebRetrieverTest.EXAMPLE_URI, false);
        sut.setResponse(WebRetrieverTest.createMockResponse(expectedContent));

        String content = sut.extractContentFromResponse();

        assertThat(content, is(equalTo(expectedContent)));
    }
}
