package com.BVT2105;

import java.io.*;
import java.net.*;
import java.util.LinkedList;

// Сканер, реализующий основные возможности приложения
public class Crawler {
    // Создадим приватные поля найденных и просмотренных ссылок
    private static LinkedList <URLDepthPair> findLink = new LinkedList <URLDepthPair>();
    private static LinkedList <URLDepthPair> viewedLink = new LinkedList <URLDepthPair>();

    // Создадим функцию возвращающая список всех пар URL-глубины, которые были посещены
    public static void showResult(LinkedList<URLDepthPair> viewedLink) {
        System.out.println("");
        for(URLDepthPair c : viewedLink) {
            String adjust = "";
            for(int i = 0; i < c.getDepth(); i++) {
                adjust = adjust.concat("  ");
            }
            System.out.println(adjust+c.getDepth() + "\tLink : "+c.getURL());
        }
    }


    // Метод форматирования запроса
    public static void request(PrintWriter out,URLDepthPair pair) throws MalformedURLException {
        String request = "GET " + pair.getPath() + " HTTP/1.1\r\nHost:" + pair.getHost() + "\r\nConnection: Close\r\n";
        out.println(request);
        out.flush();
    }

    // Метод, реализующий сканирование сайта
    public static void Process(int maxDepth) throws IOException {
        // Пока список необработанных ссылок не пуст, удаляем первый сайт, перед загрузкой его содержимого
        while(!findLink.isEmpty()) {
            URLDepthPair currentPair = findLink.removeFirst();
            // Если текущая глубина меньше максимальной, пробуем подключиться к сайту через сокет
            if(currentPair.getDepth() < maxDepth) {
                Socket my_socket;
                try {
                    my_socket = new Socket(currentPair.getHost(), 80);
                } catch (UnknownHostException e) {
                    System.out.println("Could not resolve URL: "+currentPair.getURL()+" at depth "+currentPair.getDepth());
                    continue;
                }
                my_socket.setSoTimeout(1000);
                // После подключения начинаем сканирование
                try {
                    System.out.println("Now scanning: "+currentPair.getURL()+" at depth "+currentPair.getDepth());
                    BufferedReader in = new BufferedReader(new InputStreamReader(my_socket.getInputStream()));
                    PrintWriter out = new PrintWriter(my_socket.getOutputStream(), true);
                    request(out, currentPair);
                    String line;
                    while ((line = in.readLine()) != null) {
                        if (line.indexOf(currentPair.getURLPrefix()) != -1 && line.indexOf('"') != -1) {
                            StringBuilder currentLink = new StringBuilder();
                            int i = line.indexOf(currentPair.getURLPrefix());
                            while (line.charAt(i) != '"' && line.charAt(i) != ' ') {
                                if (line.charAt(i) == '<') {
                                    currentLink.deleteCharAt(currentLink.length() - 1);
                                    break;
                                }
                                else {
                                    currentLink.append(line.charAt(i));
                                    i++;
                                }
                            }
                            System.out.println(" > Found new link: "+currentLink.toString());
                            URLDepthPair newPair = new URLDepthPair(currentLink.toString(), currentPair.getDepth() + 1);
                            if (currentPair.check(findLink, newPair) && currentPair.check(viewedLink, newPair) && !currentPair.getURL().equals(newPair.getURL()))
                                findLink.add(newPair);
                        }
                    }
                    my_socket.close();
                } catch (SocketTimeoutException e) {
                    my_socket.close();
                }
            }
            viewedLink.add(currentPair);
        }
        showResult(viewedLink);
    }

    // Основной метод, точка входа
    public static void main(String[] args) {
        //http://crawler-test.com/
        // программа принимает два параметра - URL страницу и глубину
        String[] test = new String[]{"http://crawler-test.com/", "3"};

        try {
            findLink.add(new URLDepthPair(test[0], 0));
            Process(Integer.parseInt(test[1]));
        } catch (Exception e) {
            System.out.println("Error!\n"+e);
            System.out.println("Usage: java crawler <site> <depth>");
        }
    }
}