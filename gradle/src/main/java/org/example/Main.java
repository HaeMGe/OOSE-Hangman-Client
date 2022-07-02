package org.example;
import okhttp3.OkHttpClient;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    static String name;  //Name des eingeloggten Nutzers
    static int poolID;   // Pool-ID von Pool in dem Nutzer gerade ist
    static OkHttpClient client = new OkHttpClient();  //verwendeter Client
    static PostClass posten = new PostClass();
    static String link;


    public static void main(String[] args) throws IOException, InterruptedException {

        System.out.println("1 eingeben für Lokal, 2 für Online");
        Scanner sc = new Scanner(System.in);

        int eingabe = sc.nextInt();

        if(eingabe == 2){
            link = "http://tuhintest.ddns.net:5741/";
        }else{
            link ="http://localhost:4567/";
        }

        String antwort = posten.doPostRequest(link+"games/hangman/start", "Hallo vom Client!");

        System.out.println(antwort);
        Ablauf.start();



    }

}