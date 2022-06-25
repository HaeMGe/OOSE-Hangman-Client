package org.example;
import okhttp3.OkHttpClient;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    static String name;
    static int poolID;
    static OkHttpClient client = new OkHttpClient();  //verwendeter Client
    static PostClass posten = new PostClass();
    Scanner sc = new Scanner(System.in);
    GetClass getter = new GetClass();


    String wort;
    public static void main(String[] args) throws IOException, InterruptedException {
        String antwort = posten.doPostRequest("http://localhost:4567/games/hangman/start" , "Hallo vom Client!");
        System.out.println(antwort);
        Ablauf.start();



    }

}