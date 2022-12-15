package org.example;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class URLPool {
    public Queue<URLDepthPair> notVisited = new LinkedList<>() {
    };
    public HashSet<URLDepthPair> visited = new HashSet<>();
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
        if(!visited.contains(pair)) {
            visited.add(pair);
            if (pair.getDepth() < maxDepth) {
                notVisited.add(pair);
                notify();
            }
        }
    }

    public synchronized int getWaitingWorkers() {
        return waitingThreads;
    }

    public HashSet<URLDepthPair> getResult() {
        return visited;
    }

}