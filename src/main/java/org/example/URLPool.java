package org.example;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class URLPool {
    public ConcurrentLinkedQueue<URLDepthPair> notVisited = new ConcurrentLinkedQueue<>() {
    };
    public ConcurrentHashMap<URLDepthPair, Boolean> visited = new ConcurrentHashMap<>();
    int maxDepth;
    AtomicInteger waitingThreads = new AtomicInteger(0);

    public URLPool(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public URLDepthPair getPair() {
        System.out.println(waitingThreads.get());
        waitingThreads.getAndIncrement();
        URLDepthPair pair;
        while (true) {
            if ((pair = notVisited.poll()) != null){
                waitingThreads.getAndDecrement();
                return pair;
            }
        }
    }

    public void addPair(URLDepthPair pair) {
        if(!visited.containsKey(pair)) {
            visited.put(pair, true);
            if (pair.getDepth() < maxDepth) {
                notVisited.add(pair);
            }
        }
    }

    public int getWaitingWorkers() {
        return waitingThreads.get();
    }

    public ConcurrentHashMap<URLDepthPair, Boolean> getResult() {
        return visited;
    }

}