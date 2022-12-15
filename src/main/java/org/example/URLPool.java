package org.example;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class URLPool {
    public ConcurrentLinkedQueue<URLDepthPair> notVisited = new ConcurrentLinkedQueue<>();
    public ConcurrentHashMap<URLDepthPair, Boolean> visited = new ConcurrentHashMap<>();
    int maxDepth;
    int threadKeeper = 0;

    public URLPool(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public synchronized URLDepthPair getPair() {
        while (notVisited.size() == 0) {
            threadKeeper++;
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println("");
            }
            threadKeeper--;
        }
        return notVisited.poll();
    }

    public synchronized void addPair(URLDepthPair pair) {
        if(!visited.containsKey(pair)) {
            visited.put(pair, true);
            if (pair.getDepth() < maxDepth) {
                notVisited.add(pair);
                notify();
            }
        }
    }

    public synchronized int getWait() {
        return threadKeeper;
    }

    public ConcurrentHashMap<URLDepthPair, Boolean> getResult() {
        return visited;
    }

}