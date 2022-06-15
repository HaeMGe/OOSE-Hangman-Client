package org.example;
import okhttp3.OkHttpClient;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    static OkHttpClient client = new OkHttpClient();  //verwendeter Client
    static PostClass posten = new PostClass();
    Scanner sc = new Scanner(System.in);
    GetClass getter = new GetClass();

    String wort;
    public static void main(String[] args) throws IOException {
        System.out.println("Hello world!");
        String antwort = posten.doPostRequest("http://localhost:4567/games/hangman/start" , "Hallo vom Client!");
        System.out.println(antwort);

    }


  /**  public void setWort() throws IOException {
        System.out.println("Ihr Wort:");
        this.wort = sc.next();
        String antwort = posten.doPostRequest("http://localhost:4567/games/hangman/start/neuesWort", "{ "+ wort + " }");

    }
   */
  /**  public void getWort() throws IOException {
        String raetsel = getter.run("http://localhost:4567/games/hangman/start/loesen");
        System.out.println(raetsel.length());
    }
   */


}