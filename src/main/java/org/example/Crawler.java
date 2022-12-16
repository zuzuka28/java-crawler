package org.example;
import java.net.MalformedURLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Crawler {

    public static void showResult(ConcurrentHashMap<URLDepthPair, Boolean> source) {
        for (Map.Entry<URLDepthPair, Boolean> item : source.entrySet()){
            System.out.println(item.getKey());
        }
    }

    private static Thread newTask(URLPool pool){
        return new Thread(new CrawlerTask(pool));
    }

    public static void createWorkers(URLPool pool, int numThreads){
        for (int i = 0; i < numThreads; i++) {
            Thread taskWorker = newTask(pool);
            taskWorker.start();
        }
    }

//    args
//    [0] - Начальный сайт
//    [1] - Максимальная глубина поиска
//    [2] - Максимальная количество worker'ов
    public static void main(String[] args) {
    //    args = new String[]{"https://en.wikipedia.org/wiki/Wiki", "1", "10"};
    //    args = new String[]{"https://mtuci.ru", "2", "25"};
    //    args = new String[]{"https://stackoverflow.com/questions/5244782/java-concurrent-queries", "2", "50"};
        args = new String[]{"https://habr.com/", "3", "100"};

        if (args.length == 3) {
            String startUrl = args[0];
            int maxDepth = Integer.parseInt(args[1]);
            int numThreads = Integer.parseInt(args[2]);

            URLPool pool = new URLPool(maxDepth);

            try {
                pool.addPair(new URLDepthPair(startUrl, 0));
            } catch (MalformedURLException e) {
                System.out.println("Invalid start URL");
            }

            createWorkers(pool, numThreads);

    //        данная часть кода отвечает за то, что бы во время остановить выполнение программы
    //        идея заключается в том, что если очередь пуста
    //        (то есть количество ожидающих задания потоков равно их количеству)
    //        то программа заканчивает работу и выводит результаты
            while (pool.getWaitingWorkers() != numThreads) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ignored) {
                }
            }

            try {
                showResult(pool.getResult());;
            } catch (NullPointerException ignored) {
            }
            System.exit(0);

        } else {
            System.out.println("usage: java Crawler <URL> <maximum_depth> <num_threads> or second/third not digit");
        }
    }
}

