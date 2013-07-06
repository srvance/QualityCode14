package com.vance.qualitycode;

import java.io.IOException;
import java.net.URISyntaxException;

public class WebRetriever {

    public void retrieve(String[] URIs) throws IOException, URISyntaxException {
        boolean writeToFile = false;
        Target currentTarget;

        for (String URI : URIs) {
            if ("-O".equals(URI)) {
                writeToFile = true;
                continue;
            }
            currentTarget = createTarget(URI, writeToFile);
            currentTarget.retrieve();
            writeToFile = false;
        }
    }

    protected Target createTarget(String URI, boolean writeToFile) throws URISyntaxException {
        return new Target(URI, writeToFile);
    }

    public static void main(String[] args) {
        WebRetriever retriever = new WebRetriever();
        try {
            retriever.retrieve(args);
        } catch (IOException e) {
            System.err.println("Houston, we have a problem.");
            e.printStackTrace();
        } catch (URISyntaxException e) {
            System.err.println("Bad URL!");
            e.printStackTrace();
        }
    }

}
