package com.vance.qualitycode;

import org.junit.Test;

import static org.junit.Assert.*;

public class WebRetrieverTest {
    @Test
    public void testWebRetriever() {
        assertNotNull(new WebRetriever());
    }

    @Test
    public void testRetrieve_SingleURI() {
        WebRetriever sut = new WebRetriever();

        String content = sut.retrieve("http://www.example.com");

        assertNotNull(content);
    }
}
