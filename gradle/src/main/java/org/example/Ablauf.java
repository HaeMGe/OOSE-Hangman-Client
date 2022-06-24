package org.example;

import java.io.IOException;
import java.util.Scanner;

public class Ablauf {
    static Scanner sc = new Scanner(System.in);


public static void start() throws IOException {
    System.out.println("Name: ");   //sich mit Name einloggen/sich einen Spielernamen geben
    Main.name = sc.next();
    String antwortServer = Main.posten.doPostRequest("http://localhost:4567/games/hangman/start/neuerNutzer", "{ 'name': '"+ Main.name+"'}"); //Namen für Nutzerliste an Server schicken
    System.out.println(antwortServer);
    menue1();
}

    private static void menue1() throws IOException {
        System.out.println("Was möchten Sie tuen?");
        System.out.println("1: Spielpool beitreten");
        System.out.println("2: Spielpool anlegen");
        System.out.println("3. Logout");
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
    }

    //logout
    private static void logout() throws IOException {
        start();
    }

    //neuen Pool anlegen
    private static void poolAnlegen() throws IOException {
        System.out.println("Welchen Schwierigkeitsgrad soll der Pool haben?");
        int level = sc.nextInt();
        System.out.println("Was soll die Pool-ID sein?");
        int id = sc.nextInt();

        String antwort = Main.posten.doPostRequest("http://localhost:4567/games/hangman/start/neuerPool/", "{ 'name': '"+ Main.name+ "','pool': '"+id+"','level': '"+level+"'}");  //neuen Postrequest mit Eingabe an Server
        boolean antwort2 = Boolean.parseBoolean(antwort);

        if(antwort2){
            System.out.println("Ein Pool wurde erfolgreich angelegt.");
            menue1();
        }
        else System.out.println("Leider ist ein Fehler passiert. Probieren Sie eventuell eine andere ID aus.");
        menue1();

    }

    private static void poolBeitreten() throws IOException {
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
            }
            else {
                System.out.println("Leider gab es Probleme beim Beitreten. Sind Sie eventuell bereits Mitglied in diesem Pool?");
                }
            }
        }
        menue1();

    }

    public static void raten() throws IOException {
     String eingabe = null;
     System.out.println("Bitte geben Sie die 0 ein, wenn Sie einen Buchstaben erraten möchten und eine 1, wenn Sie schon ein ganzes Wort probieren wollen.");
     int option = sc.nextInt();
     if(option == 0){
         System.out.println("Welchen Buchstaben möchsten Sie ausprobieren?");
         eingabe = sc.next();
     }
     if (option == 1){
         System.out.println("Welches Wort wollen Sie ausprobieren?");
         eingabe = sc.next();
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

