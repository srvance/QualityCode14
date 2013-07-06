package com.vance.qualitycode;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class TargetTest {
    public static final String EXAMPLE_DOMAIN = "www.example.com";
    public static final String EXAMPLE_URI = Target.SCHEME_HTTP + "://" + EXAMPLE_DOMAIN;

    @Test
    public void testRetrieve() throws IOException, URISyntaxException {
        final String expectedContent = "This is one set of content";
        final OutputStream outputStream = new ByteArrayOutputStream();
        Target sut = new Target(EXAMPLE_URI, false) {
            @Override
            protected void retrieveResponse() throws IOException, URISyntaxException {
                setResponse(WebRetrieverTest.createMockResponse(expectedContent));
            }

            @Override
            protected OutputStream determineOutputStream() {
                return outputStream;
            }
        };

        sut.retrieve();

        String content = outputStream.toString();
        assertThat(content, is(notNullValue()));
        assertThat(content, is(equalTo(expectedContent)));
    }

    @Test
    public void testTarget_SchemeDomain() throws IOException, URISyntaxException {
        String expectedOriginal = EXAMPLE_URI;

        Target sut = new Target(expectedOriginal, false);

        assertThat(sut.getOriginal(), is(expectedOriginal));
        assertThat(sut.getOutputToFile(), is(false));
        URI actualURI = sut.getUri();
        assertThat(actualURI.getScheme(), is(equalTo(Target.SCHEME_HTTP)));
        assertThat(actualURI.getHost(), is(equalTo(EXAMPLE_DOMAIN)));
    }

    @Test
    public void testTarget_DomainOnly() throws IOException, URISyntaxException {
        String expectedOriginal = EXAMPLE_DOMAIN;

        Target sut = new Target(expectedOriginal, false);

        assertThat(sut.getOriginal(), is(expectedOriginal));
        assertThat(sut.getOutputToFile(), is(false));
        URI actualURI = sut.getUri();
        assertThat(actualURI.getHost(), is(equalTo(expectedOriginal)));
        assertThat(actualURI.getScheme(), is(equalTo(Target.SCHEME_HTTP)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTarget_NonHttpScheme() throws IOException, URISyntaxException {
        new Target("ftp://" + EXAMPLE_DOMAIN, false);
    }

    @Test
    public void testEmit() throws IOException, URISyntaxException {
        String expectedContent = "This is another set of content";
        final OutputStream outputStream = new ByteArrayOutputStream();
        Target sut = new Target(EXAMPLE_URI, false) {
            @Override
            protected OutputStream determineOutputStream() {
                return outputStream;
            }
        };
        sut.setResponse(WebRetrieverTest.createMockResponse(expectedContent));

        sut.emit();

        assertThat(outputStream.toString(), is(equalTo(expectedContent)));
    }

    @Test
    public void testRetrieve_StandardOutput() throws IOException, URISyntaxException {
        final String expectedContent = "This should go to stdout";
        Target sut = new Target(EXAMPLE_URI, false) {
            boolean copiedToOutput = false;

            @Override
            public void retrieve() throws IOException, URISyntaxException {
                super.retrieve();
                assertThat(copiedToOutput, is(true));
            }

            @Override
            protected void retrieveResponse() throws IOException, URISyntaxException {
                setResponse(WebRetrieverTest.createMockResponse(expectedContent));
            }

            @Override
            protected void copyToOutput(InputStream content, OutputStream output) throws IOException {
                assertThat(System.out, is(output));
                copiedToOutput = true;
            }
        };

        sut.retrieve();
    }
}
