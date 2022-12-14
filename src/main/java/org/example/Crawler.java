package org.example;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.lang.model.type.NullType;
import java.net.*;
import java.util.*;
import java.io.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Crawler {

    public ConcurrentLinkedQueue<URLDepthPair> notVisited = new ConcurrentLinkedQueue<>();
    public ConcurrentHashMap<URLDepthPair, Boolean> visited = new ConcurrentHashMap<>();

    public Crawler(){

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
                newUrls.add(tag.absUrl("href"));
            }
        } catch (IOException e){
            System.out.println(e.getMessage());
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
                System.out.println(e.getMessage());
            }
        }
        return converted;
    }

    private LinkedList<URLDepthPair> filterNotVisitedUrls(LinkedList<URLDepthPair> urls){
        LinkedList<URLDepthPair> notVisited = new LinkedList<>();
        for (URLDepthPair pair: urls){
            if (!visited.containsKey(pair)){
                notVisited.add(pair);
            }
        }
        return notVisited;
    }

    private void addNewUrlsFromUrl(URLDepthPair pair, int maxDepth){
        Connection connection = makeRequest(pair);

        if (pair.getDepth() < maxDepth){
            LinkedList<String> urlsFromSource = getNewUrls(connection);
            visited.put(pair, true);
            LinkedList<URLDepthPair> urlsToAdd = filterNotVisitedUrls(
                    convertNewUrlsToPairs(
                            urlsFromSource,
                            pair.getDepth()+1
                    )
            );
            notVisited.addAll(
                    urlsToAdd
            );
        }
    }


    public void process(String pair, int maxDepth) throws IOException {
        notVisited.add(new URLDepthPair(pair, 0));
        System.out.println(notVisited);

        while (!notVisited.isEmpty()){
            URLDepthPair currentPair = notVisited.poll();
            addNewUrlsFromUrl(currentPair, maxDepth);
            System.out.println(visited);
        }
    }

//    args
//    [0] - Начальный сайт
//    [1] - Максимальная глубина поиска
    public static void main(String[] args) {
        String[] arg = new String[]{"http://go.com","4"};

        String start = arg[0];
        int maxDepth = Integer.parseInt(arg[1]);

        Crawler crawler = new Crawler();

        try {
            crawler.process(start, maxDepth);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}

