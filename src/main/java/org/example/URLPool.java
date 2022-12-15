package org.example;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class URLPool {
    public ConcurrentLinkedQueue<URLDepthPair> notVisited = new ConcurrentLinkedQueue<>();
    public ConcurrentHashMap<URLDepthPair, Boolean> visited = new ConcurrentHashMap<>();
    int maxDepth;
    int cWait = 0;

    public URLPool(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public synchronized URLDepthPair getPair() {
        while (notVisited.size() == 0) {
            cWait++;
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println("");
            }
            cWait--;
        }
        return notVisited.poll();
    }

    public synchronized void addPair(URLDepthPair pair) {
        if(URLDepthPair.check(visited, pair)) {
            visited.put(pair, true);
            if (pair.getDepth() < maxDepth) {
                notVisited.add(pair);
                notify();
            }
        }
    }

    public synchronized int getWait() {
        return cWait;
    }

    public ConcurrentHashMap<URLDepthPair, Boolean> getResult() {
        return visited;
    }

}