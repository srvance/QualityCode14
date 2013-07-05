package com.vance.qualitycode;

import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class WebRetriever {

    public List<String> retrieve(String[] URIs) throws IOException, URISyntaxException {
        List<String> contents = new ArrayList<String>(URIs.length);
        boolean writeToFile = false;
        Target currentTarget;

        for (String URI : URIs) {
            if ("-O".equals(URI)) {
                writeToFile = true;
                continue;
            }
            currentTarget = createTarget(writeToFile, URI);
            retrieve(currentTarget);
            contents.add(currentTarget.getContent());
            currentTarget.emit();
            writeToFile = false;
        }

        return contents;
    }

    protected Target createTarget(boolean writeToFile, String URI) throws URISyntaxException {
        return new Target(URI, writeToFile);
    }

    public void retrieve(Target target) throws IOException, URISyntaxException {
        target.retrieveResponse();

        target.extractContentFromResponse();
    }

    public static void main(String[] args) {
        WebRetriever retriever = new WebRetriever();
        try {
            List<String> contents = retriever.retrieve(args);
            System.out.println(StringUtils.join(contents, '\n'));
        } catch (IOException e) {
            System.err.println("Houston, we have a problem.");
            e.printStackTrace();
        } catch (URISyntaxException e) {
            System.err.println("Bad URL!");
            e.printStackTrace();
        }
    }

}
