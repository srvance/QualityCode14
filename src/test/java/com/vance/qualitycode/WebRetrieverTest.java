package com.vance.qualitycode;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class WebRetrieverTest {
    @Test
    public void testWebRetriever() {
        WebRetriever sut = new WebRetriever();

        assertNotNull(sut);
    }

    @Test
    public void testRetrieve_SingleURI() throws IOException {
        WebRetriever sut = new WebRetriever();

        String content = sut.retrieve("http://www.example.com");

        assertNotNull(content);
        assertTrue(content.contains("<html>"));
    }
}
