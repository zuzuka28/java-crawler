package org.example;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.LinkedList;

public class CrawlerTask implements Runnable{
    URLPool urlPool;

    public CrawlerTask(URLPool pool) {
        this.urlPool = pool;
    }

    private Connection makeRequest(URLDepthPair pair) {
        return Jsoup.connect(pair.getUrl().toString());
    }

    private LinkedList<String> getNewUrls(Connection connection){
        LinkedList<String> newUrls = new LinkedList<>();
        try {
            Document doc = connection.get();
            Elements linkTags = doc.select("a");
            for (Element tag : linkTags) {
                newUrls.add(tag.attr("abs:href"));
            }
        } catch (IOException e){
//            System.out.println(e.getMessage());
        }
        return newUrls;
    }

    private LinkedList<URLDepthPair> convertNewUrlsToPairs(LinkedList<String> urls, int depth){
        LinkedList<URLDepthPair> converted = new LinkedList<>();

        for (String item : urls){
            try {
                URLDepthPair pair = new URLDepthPair(item, depth);
                converted.add(pair);
            } catch (MalformedURLException e){
//                System.out.println(e.getMessage());
            }
        }
        return converted;
    }


    public void process(URLDepthPair pair) throws IOException {
        Connection connection = makeRequest(pair);
        LinkedList<String> urlsFromSource = getNewUrls(connection);
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
                throw new RuntimeException(e);
            }

        }
    }
}