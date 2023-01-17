package com.BVT2105;

import java.util.LinkedList;
import java.net.MalformedURLException;
import java.net.URL;

//URL ссылки и их глубина
public class URLDepthPair {
    private static final String URL_PREFIX = "http://";
    private String URLString;
    private int depth;


    // Напишем конструктор
    public URLDepthPair (String URL, int depth){
        this.URLString=URL;
        this.depth=depth;
    }

    //get метод для хоста
    public String getHost() throws MalformedURLException {
        URL host = new URL(URLString);
        return host.getHost();
    }

    //get метод пути
    public String getPath() throws MalformedURLException {
        URL path = new URL(URLString);
        return path.getPath();
    }

    //get метод для глубины
    public int getDepth() {
        return depth;
    }

    //get метод для URL
    public String getURL() {
        return URLString;
    }

    //get метод для URL prefix
    public String getURLPrefix() {
        return URL_PREFIX;
    }

    //Функция, проверяющая, выполняли ли мы поиск по URL
    public static boolean check(LinkedList<URLDepthPair> resultLink, URLDepthPair pair) {
        boolean hasBeenChecked = true;
        for (URLDepthPair c : resultLink) {
            if (c.getURL().equals(pair.getURL())) hasBeenChecked = false;
        }
        return hasBeenChecked;
    }
}