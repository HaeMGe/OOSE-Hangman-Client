package org.example;
import okhttp3.OkHttpClient;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    static String name;  //Name des eingeloggten Nutzers
    static int poolID;   // Pool-ID von Pool in dem Nutzer gerade ist
    static OkHttpClient client = new OkHttpClient();  //verwendeter Client
    static PostClass posten = new PostClass();
    static String link = "http://localhost:4567/";


    public static void main(String[] args) throws IOException, InterruptedException {

        String antwort = posten.doPostRequest("http://localhost:4567/games/hangman/start", "Hallo vom Client!");

        System.out.println(antwort);
        Ablauf.start();



    }

}