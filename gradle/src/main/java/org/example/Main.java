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
    //http://tuhintest.ddns.net:5741/


    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Herzlich Willkommen in einer Welt voller Spiel, Spass und Spannung!");
        Ablauf.start();
    }

}