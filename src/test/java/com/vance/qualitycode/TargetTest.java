package com.vance.qualitycode;

import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class TargetTest {
    public static final String EXAMPLE_DOMAIN = "www.example.com";
    public static final String EXAMPLE_URI = Target.SCHEME_HTTP + "://" + EXAMPLE_DOMAIN;

    @Test
    public void testRetrieve_SingleTarget() throws IOException, URISyntaxException {
        final String expectedContent = "This is one set of content";
        Target target = new Target(EXAMPLE_URI, false) {
            @Override
            protected void retrieveResponse() throws IOException, URISyntaxException {
                setResponse(WebRetrieverTest.createMockResponse(expectedContent));
            }
        };

        target.retrieve();

        String content = target.getContent();
        assertThat(content, is(notNullValue()));
        assertThat(content, is(equalTo(expectedContent)));
    }

    @Test
    public void testWebRetrieverTarget_SchemeDomain() throws IOException, URISyntaxException {
        String expectedOriginal = EXAMPLE_URI;

        Target sut = new Target(expectedOriginal, false);

        assertThat(sut.getOriginal(), is(expectedOriginal));
        assertThat(sut.getOutputToFile(), is(false));
        URI actualURI = sut.getUri();
        assertThat(actualURI.getScheme(), is(equalTo(Target.SCHEME_HTTP)));
        assertThat(actualURI.getHost(), is(equalTo(EXAMPLE_DOMAIN)));
    }

    @Test
    public void testWebRetrieverTarget_DomainOnly() throws IOException, URISyntaxException {
        String expectedOriginal = EXAMPLE_DOMAIN;

        Target sut = new Target(expectedOriginal, false);

        assertThat(sut.getOriginal(), is(expectedOriginal));
        assertThat(sut.getOutputToFile(), is(false));
        URI actualURI = sut.getUri();
        assertThat(actualURI.getHost(), is(equalTo(expectedOriginal)));
        assertThat(actualURI.getScheme(), is(equalTo(Target.SCHEME_HTTP)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWebRetrieverTarget_NonHttpScheme() throws IOException, URISyntaxException {
        new Target("ftp://" + EXAMPLE_DOMAIN, false);
    }

    @Test
    public void testTargetExtractContentFromResponse() throws IOException, URISyntaxException {
        String expectedContent = "This is another set of content";
        Target sut = new Target(EXAMPLE_URI, false);
        sut.setResponse(WebRetrieverTest.createMockResponse(expectedContent));

        String content = sut.extractContentFromResponse();

        assertThat(content, is(equalTo(expectedContent)));
    }
}
