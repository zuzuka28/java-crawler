package org.example;

import java.net.MalformedURLException;
import java.net.URL;

public class URLDepthPair {
    private final int depth;

    private final URL url;

    public URLDepthPair(String URL, int depth) throws MalformedURLException{
        this.depth = depth;
        if (!checkUrl(URL)){
            throw new MalformedURLException(String.format("not valid URL: %s", URL));
        }
        this.url = new URL(URL);
    }

    private boolean checkUrl(String source) {
        return source != null &&
                source.matches("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
    }

    public int getDepth() {
        return depth;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof URLDepthPair pair)) return false;

        return getUrl().equals(pair.getUrl());
    }

    @Override
    public int hashCode() {
        return getUrl().hashCode();
    }

    public URL getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return String.format("%s with depth %s", url, depth);
    }


}
