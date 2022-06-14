package org.example;

import okhttp3.OkHttpClient;

import java.io.IOException;

public class Main {
    static OkHttpClient client = new OkHttpClient();  //verwendeter Client
    static PostClass posten = new PostClass();
    public static void main(String[] args) throws IOException {
        System.out.println("Hello world!");
        String antwort = posten.doPostRequest("http://localhost:4567/games/hangman/start" , "Hallo vom Client!");
        System.out.println(antwort);

    }


}