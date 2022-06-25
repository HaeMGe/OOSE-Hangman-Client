package org.example;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Ablauf {
    static Scanner sc = new Scanner(System.in);


public static void start() throws IOException, InterruptedException {
    System.out.println("Name: ");   //sich mit Name einloggen/sich einen Spielernamen geben
    Main.name = sc.next();
    String antwortServer = Main.posten.doPostRequest("http://localhost:4567/games/hangman/start/neuerNutzer", "{ 'name': '"+ Main.name+"'}"); //Namen für Nutzerliste an Server schicken
    if(antwortServer.contains("true")){
        System.out.println("Anmeldung erfolgreich. Viel Spass beim Spielen!");
        menue1();
    }
    else {
        System.out.println("Anmeldung gescheitert. Es ist im Moment bereits ein Nutzer mit diesem Namen registriert.");
        start();
    }
   /** System.out.println(antwortServer);
    JsonObject jObj = new Gson().fromJson(antwortServer, JsonObject.class);
    String text = jObj.get("text").toString();
    text = text.replace("\"", "");

    if(text.equals("Herzlich Willkommen vom Server!")){
        System.out.println("---Mit dem Server verbunden---");
    }else {
        System.err.println("----Fehler beim verbinden----");
    }
    menue1();
    */
}

    private static void menue1() throws IOException, InterruptedException {
        System.out.println("Was möchten Sie tuen?");
        System.out.println("1: Spielpool beitreten");
        System.out.println("2: Spielpool anlegen");
        System.out.println("3. Logout");
        System.out.println("4. Pools, denen ich angehoere");
        int option = sc.nextInt();
    if(option == 1){
        poolBeitreten();
    }
    if(option == 2){
        poolAnlegen();
    }
    if(option == 3){
        logout();
    }
    if(option == 4){
        meinePools();
    }
    }

    //logout
    private static void logout() throws IOException, InterruptedException {
    Main.name = "";
        start();
    }

    public static void meinePools() throws IOException, InterruptedException {
        String antwort = Main.posten.doPostRequest("http://localhost:4567/games/hangman/start/meinePools/", "{ 'name': '"+ Main.name+ "'}");  //neuen Postrequest mit Eingabe an Server
        System.out.println(antwort);
        menue1();

    }
    //neuen Pool anlegen
    private static void poolAnlegen() throws IOException, InterruptedException {
        System.out.println("Sie haben die Wahl, wechen Schwierigkeitsgrad der Pool haben soll: ");
        System.out.println("1: Anfänger");
        System.out.println("2: Clevere");
        System.out.println("3: Profis");
        System.out.println("4: Absolute Überflieger");
        int level = sc.nextInt();
        System.out.println("Was soll die Pool-ID sein?");
        int id = sc.nextInt();

        String antwort = Main.posten.doPostRequest("http://localhost:4567/games/hangman/start/neuerPool/", "{ 'name': '"+ Main.name+ "','pool': '"+id+"','level': '"+level+"'}");  //neuen Postrequest mit Eingabe an Server
        boolean antwort2 = Boolean.parseBoolean(antwort);

        if(antwort2){
            System.out.println("Ein Pool wurde erfolgreich angelegt.");
            //menue1();
        }
        else System.out.println("Leider ist ein Fehler passiert. Probieren Sie eventuell eine andere ID aus.");
        menue1();

    }

    private static void poolBeitreten() throws IOException, InterruptedException {
        String antwort = Main.posten.doPostRequest("http://localhost:4567/games/hangman/start/poolSuchen/", "pools angefragt");
        String[] antwortSplit = antwort.split("Vorhanden: ");
        if(antwortSplit[1].contains("true"))  {
            System.out.println("Leider kein Spielpool vorhanden. Sie könnten selbst einen erstellen...");
        }
        else {
            String[] liste = antwortSplit[0].split("ID: ");

            for(int i = 1; i < liste.length; i++) {
                System.out.print(i + ". ID: ");
                System.out.println(liste[i]);
            }
        System.out.println("Geben Sie die ID Ihres Wunschpools ein oder die -1 für das Hauptmenü");
        int wunschId = sc.nextInt();
        if(wunschId == -1){  //zurück zu Menü
            menue1();
        }
        else {
            String antwortServer = Main.posten.doPostRequest("http://localhost:4567/games/hangman/start/beitreten/", "{ 'name': '" + Main.name + "','pool': '" + wunschId + "'}");  //neuen Postrequest mit Eingabe an S
            if(antwortServer.contains("true")){
                System.out.println("Sie sind dem Pool erfolgreich beigetreten");
                Main.poolID = wunschId;
                poolWarteRaum();
            }
            else {
                System.out.println("Leider gab es Probleme beim Beitreten. Sind Sie eventuell bereits Mitglied in diesem Pool?");
                }
            }
        }
        menue1();
    }


    public static void poolWarteRaum() throws InterruptedException, IOException {

        boolean spielGestartet = false;
        int sekunden = 0;
        String text = "";

        System.out.println("---Warte auf zweiten Spieler---");
        while(!spielGestartet){
            TimeUnit.SECONDS.sleep(1);
            sekunden = sekunden+1;

            String antwort = Main.posten.doPostRequest("http://localhost:4567/games/hangman/start/pool/warteRaum", "{ 'poolID':"+Main.poolID+" }");
            antwort = antwort.replace("{", "");
            antwort = antwort.replace("}", "");

            spielGestartet = Boolean.parseBoolean(antwort);
            System.out.println(spielGestartet);

            if(sekunden%3==0){
                text = text+"*";
                System.out.println(text);
            }
            if(sekunden%15==0){
                text =  "";
            }
        }

        //hier wurde das Spiel gestartet
            spiel();

    }

    public static void spiel() throws IOException {

        String antwort = Main.posten.doPostRequest("http://localhost:4567/games/hangman/start/spiel/anfang", "{ 'poolID':"+Main.poolID+",''name':'"+Main.name+"' }");

    //je nach antwort des servers, entweder raten etc, oder warten bis man dran ist durch statusabfragen --> polling()

    }

    public static void polling() throws IOException {
        String antwort = Main.posten.doPostRequest("http://localhost:4567/games/hangman/start/spiel/status", "{ 'poolID':"+Main.poolID+",''name':'"+Main.name+"' }");
    }


    public static void raten() throws IOException {
        String eingabe = null;
        System.out.println("Bitte geben Sie die 0 ein, wenn Sie einen Buchstaben erraten möchten und eine 1, wenn Sie schon ein ganzes Wort probieren wollen.");
        int option = sc.nextInt();
        if (option == 0) {
            boolean x = true;
            while (x == true) {  // Falls Eingabe ungültig, wird Eingabe wiederholt
                System.out.println("Welchen Buchstaben wollen Sie ausprobieren?");
                eingabe = sc.next();
                char C = eingabe.charAt(0);
                if (!((C >= 'a' && C <= 'z') || (C >= 'A' && C <= 'Z'))) {   //überprüft,ob Eingabe gültig ist
                    System.out.println("Eingabe nicht korrekt. Bitte geben Sie einen Buchstaben ein!");
                } else {
                    x = false;
                }
            }
        }
        if (option == 1) {
            boolean x = false;
            while (x == false) {
                System.out.println("Welches Wort wollen Sie ausprobieren?");
                eingabe = sc.next();

                x = eingabe.matches("[a-zA-Z]+");  //Überprüft, ob Wort nur Buchstaben enthält

                if (x == false) {
                    System.out.println("Bitte nochmal eingeben. Das Wort darf nur Buchstaben enthalten");  //Wenn Wort nicht nur Buchstaben enthält, wird Eingabe wiederholt
                } else {
                }
            }
        }


     String antwort = Main.posten.doPostRequest("http://localhost:4567/games/hangman/start/neuesWort/"+ option, "{ 'name': '"+ Main.name+ "','pool': '"+Main.poolID+"','zeichen': '"+eingabe+"'}");  //neuen Postrequest mit Eingabe an Server
     antwort.replace("{", "");
     antwort.replace("}", "");
     boolean antwort2 = Boolean.parseBoolean(antwort);
     System.out.println(antwort);
     System.out.println(antwort2);
     if(antwort2){
         System.out.println("Richtig geraten!");
     }
     else System.out.println("Leider falscher Buchstabe! :-(");


 }
 }

