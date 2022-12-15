package org.example;

import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class URLPool {
    public Queue<URLDepthPair> notVisited = new ConcurrentLinkedQueue<>() {
    };
    public HashMap<URLDepthPair, Boolean> visited = new HashMap<>();
    int maxDepth;
    int waitingThreads = 0;

    public URLPool(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public synchronized URLDepthPair getPair() {
        while (notVisited.isEmpty()) {
            waitingThreads++;
            try {
                wait();
            } catch (InterruptedException ignore) {
            }
            waitingThreads--;
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

    public synchronized int getWaitingWorkers() {
        return waitingThreads;
    }

    public HashMap<URLDepthPair, Boolean> getResult() {
        return visited;
    }

}