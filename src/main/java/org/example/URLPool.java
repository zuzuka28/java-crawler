package org.example;

import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class URLPool {
    public Queue<URLDepthPair> notVisited = new ConcurrentLinkedQueue<>() {
    };
    public HashMap<URLDepthPair, Boolean> visited = new HashMap<>();
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

    public HashMap<URLDepthPair, Boolean> getResult() {
        return visited;
    }

}