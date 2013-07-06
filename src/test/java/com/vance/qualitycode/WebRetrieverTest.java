package com.vance.qualitycode;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.easymock.EasyMock;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class WebRetrieverTest {

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
        final OutputStream outputStream = new ByteArrayOutputStream();

        WebRetriever sut = new WebRetriever() {
            int siteIndex = 0;

            @Override
            protected Target createTarget(String URI, boolean writeToFile) throws URISyntaxException {
                return new Target(URI, writeToFile) {
                    @Override
                    protected void retrieveResponse() throws IOException, URISyntaxException {
                        setResponse(createMockResponse(expectedContent[siteIndex++]));
                    }

                    @Override
                    protected OutputStream determineOutputStream() {
                        return outputStream;
                    }
                };
            }
        };

        String[] sites = {"site1", "site2", "site3"};
        sut.retrieve(sites);

        assertThat(outputStream.toString(), is(equalTo(StringUtils.join(expectedContent, ""))));
    }

    @Test
    public void testRetrieve_SingleURLOutputToFile() throws IOException, URISyntaxException {
        final String expectedContent = "This content should go to a file";
        final OutputStream outputStream = new ByteArrayOutputStream();
        String[] args = {"-O", TargetTest.EXAMPLE_URI};
        WebRetriever sut = new WebRetriever() {
            int retrieveCount = 0;
            int emitCount = 0;

            @Override
            public void retrieve(String[] URIs) throws IOException, URISyntaxException {
                super.retrieve(URIs);
                assertTrue(emitCount > 0);
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
                    protected void emit() throws IOException {
                        super.emit();
                        assertThat(getOutputToFile(), is(true));
                        emitCount++;
                    }

                    @Override
                    protected OutputStream determineOutputStream() {
                        return outputStream;
                    }
                };
            }
        };

        sut.retrieve(args);

        assertThat(outputStream.toString(), is(equalTo(expectedContent)));
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
