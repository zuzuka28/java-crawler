package org.example;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.LinkedList;

public class CrawlerTask implements Runnable{
    URLPool urlPool;

    public CrawlerTask(URLPool pool) {
        this.urlPool = pool;
    }

    private Connection makeRequest(URLDepthPair pair) {
        return Jsoup.connect(pair.getUrl().toString());
    }

    private HashSet<String> getNewUrls(Connection connection){
        HashSet<String> newUrls = new HashSet<>();
        try {
            Document doc = connection.get();
            Elements linkTags = doc.select("a");
            for (Element tag : linkTags) {
                newUrls.add(tag.attr("abs:href"));
            }
        } catch (IOException ignored){
        }
        return newUrls;
    }

    private LinkedList<URLDepthPair> convertNewUrlsToPairs(HashSet<String> urls, int depth){
        LinkedList<URLDepthPair> converted = new LinkedList<>();

        for (String item : urls){
            try {
                URLDepthPair pair = new URLDepthPair(item, depth);
                converted.add(pair);
            } catch (MalformedURLException ignored){
            }
        }
        return converted;
    }


    public void process(URLDepthPair pair) throws IOException {
        Connection connection = makeRequest(pair);
        HashSet<String> urlsFromSource = getNewUrls(connection);
        LinkedList<URLDepthPair> urlsToAdd = convertNewUrlsToPairs(
                urlsFromSource,
                pair.getDepth() + 1
        );


        for (URLDepthPair item: urlsToAdd){
            urlPool.addPair(item);
        }

    }

    @Override
    public void run() {
        while (true){
            URLDepthPair pair = urlPool.getPair();
            try {
                process(pair);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

        }
    }
}
