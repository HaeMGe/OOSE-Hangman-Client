package org.example;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Ablauf {
    static Scanner sc = new Scanner(System.in);

    /**
     * Startet die Client-Anwendung und erfragt den Nutzernamen des Users.
     * @throws InterruptedException
     */
    public static void start()  {
        try {
            System.out.println("Name: ");   //sich mit Name einloggen/sich einen Spielernamen geben
            Main.name = sc.next();


            String antwortServer = Main.posten.doPostRequest(Main.link+"games/hangman/start/neuerNutzer", "{ 'name': '" + Main.name + "'}"); //Namen für Nutzerliste an Server schicken
            System.out.println( antwortServer);
            if (antwortServer.contains("true")) {
                System.out.println("Anmeldung erfolgreich. Viel Spass beim Spielen!");
                menue1();
            } else {
                System.out.println("Anmeldung gescheitert. Es ist im Moment bereits ein Nutzer mit diesem Namen registriert.");
                start();
            }
        }
        catch(IOException | InterruptedException i){
            System.out.println("Fehler in der Eingabe. Ein Name besteht nur aus Buchstaben!");
            start();
        }

}

    /**
     * Hauptmenue für Nutzer, mit den Optionen: Pool beitreten, Pool anlegen und logout
     * @throws InterruptedException
     */
    private static void menue1() throws InterruptedException, IOException {
        boolean loop = true;
        String option;
        int count = 0;

        System.out.println("Was möchten Sie tuen?");
        System.out.println("1: Spielpool beitreten");
        System.out.println("2: Spielpool anlegen");
        System.out.println("3. Logout");

        while(loop) {

                option = sc.nextLine();

                if(option.equals("1")){
                    loop = false;
                    poolBeitreten();
                } else if (option.equals("2")) {
                    loop = false;
                    poolAnlegen();
                } else if (option.equals("3")) {
                    loop = false;
                    logout();
                }else {
                    System.out.println("Ooops, ungueltige Eingabe.");
                }
        }
    }

    /**
     * User kann sich ausloggen und anschließend wieder mit neuem Namen anmelden
     * @throws IOException
     */
    private static void logout() throws IOException {
    Main.name = "";
        start();
    }

    /**
     * Ein neuer Pool kann angelegt werden. Der anlegende Nutzer ist automatisch Mitglied im Pool und wartet auf einen Mitspieler.
     * @throws InterruptedException
     */
    private static void poolAnlegen() throws InterruptedException, IOException {

        boolean loop = true;
        String eingabe = "";

        System.out.println("Sie haben die Wahl, wechen Schwierigkeitsgrad der Pool haben soll: ");
        System.out.println("1: Anfänger");
        System.out.println("2: Clevere");
        System.out.println("3: Profis");
        System.out.println("4: Absolute Überflieger");


        while(loop) {

            eingabe = sc.nextLine();


            if(eingabe.equals("1")){
                loop = false;
            } else if (eingabe.equals("2")) {
                loop = false;
            } else if (eingabe.equals("3")) {
                loop = false;
            }else {
                System.out.println("Ooops, ungueltige Eingabe.");
            }
        }

         int level = Integer.parseInt(eingabe);
          level = level-1;


        System.out.println("Was soll die Pool-ID sein?");

        loop = true;
        int id = -1;

        while(loop){

            eingabe = sc.nextLine();
            try {
                id = Integer.parseInt(eingabe);
            }catch(NumberFormatException n){
                System.out.println("Fehler, nur Zahlen eingeben");
            }
            if(id>=0){
                loop = false;
            }else{
                System.out.println("Fehler, keine natürliche Zahl");
            }

        }



            String antwort = Main.posten.doPostRequest(Main.link+"games/hangman/start/neuerPool/", "{ 'name': '" + Main.name + "','pool': '" + id + "','level': '" + level + "'}");  //neuen Postrequest mit Eingabe an Server
            boolean antwort2 = Boolean.parseBoolean(antwort);

            System.out.println(antwort2);
            if (antwort2) {
                System.out.println("Ein Pool wurde erfolgreich angelegt.");
                Main.poolID = id;
                poolWarteRaum();
            } else System.out.println("Leider ist ein Fehler passiert. Probieren Sie eventuell eine andere ID aus.");
            menue1();
        }


    /**
     * Es kann einem bestehenden Pool beigetreten werden. Das Spiel beginnt dann sofort, da bereits der Anleger des Pools im Pool wartet.
     * @throws InterruptedException
     */
    private static void poolBeitreten() throws InterruptedException, IOException {
        String antwort = Main.posten.doPostRequest(Main.link+"games/hangman/start/poolSuchen/", "pools angefragt");
        String[] antwortSplit = antwort.split("Vorhanden: ");
        if(antwortSplit[1].contains("true"))  {
            System.out.println("Leider kein Spielpool vorhanden. Sie könnten selbst einen erstellen...");
            menue1();
        }
        else {
            String[] liste = antwortSplit[0].split("ID: ");

            for (int i = 1; i < liste.length; i++) {
                System.out.print(i + ". ID: ");
                System.out.println(liste[i]);
            }

                System.out.println("Geben Sie die ID Ihres Wunschpools ein oder die -1 für das Hauptmenü");

                boolean loop = true;
                int wunschId = -2;
                String eingabe = "";

                while(loop){

                    eingabe = sc.nextLine();
                    try {
                        wunschId = Integer.parseInt(eingabe);
                    }catch(NumberFormatException n){
                        System.out.println("Fehler, nur Zahlen eingeben");
                    }
                    if(wunschId>=-1){
                        loop = false;
                    }else{
                        System.out.println("Fehler, keine passende Zahl");
                    }

                }


                if (wunschId == -1) {  //zurück zu Menü
                    menue1();
                } else {
                    String antwortServer = Main.posten.doPostRequest(Main.link+"games/hangman/start/beitreten/", "{ 'name': '" + Main.name + "','pool': '" + wunschId + "'}");  //neuen Postrequest mit Eingabe an S
                    if (antwortServer.contains("true")) {
                        System.out.println("Sie sind dem Pool erfolgreich beigetreten");
                        Main.poolID = wunschId;
                        poolWarteRaum();  //Nutzer kommt sofort in den Warteraum dieses Pools
                    } else {
                        System.out.println("Leider gab es Probleme beim Beitreten. Sind Sie eventuell bereits Mitglied in diesem Pool oder haben die falsche ID verwendet?");
                        menue1();
                    }
                }
        }
    }

    /**
     * Methode, in der der Client bleibt, bis ein Gegner seinem Pool beitritt (oder er irgendwann rausgeworfen wird, wenn ein bestimmtes Zeitlimit ueberschritten ist?)
     * @throws InterruptedException
     * @throws IOException
     */
    public static void poolWarteRaum() throws InterruptedException, IOException {

        boolean spielGestartet = false;
        int sekunden = 0;
        String text = "";
        Main.poolErstellt = true;

        System.out.println("---Warte auf zweiten Spieler---");
        //Prüft jede Sekunde, ob ein Gegner dem Pool beigetreten ist, und das Spiel begonnen hat
        int warteDauer = 60;
        while (!spielGestartet && warteDauer != 0) {
            warteDauer--;
            TimeUnit.SECONDS.sleep(1);
            sekunden = sekunden + 1;

            String antwort = Main.posten.doPostRequest(Main.link + "games/hangman/start/pool/warteRaum", "{ 'poolID':" + Main.poolID + " }");
            //System.out.println(antwort);
            antwort = antwort.replace("{", "");
            antwort = antwort.replace("}", "");

            //Boolean vom Server, welches angibt ob das Spiel gestartet ist oder nicht
            spielGestartet = Boolean.parseBoolean(antwort);

            //Kleine Warteanimation :D
            text = text + "*";
            System.out.print(text + " \r");
            if (sekunden % 15 == 0) {
                text = "";
            }
        }

        if (warteDauer == 0) {
            System.out.println("Leider niemand da. :(");
            //der Pool muss noch vom Server geloescht werden
            Main.posten.doPostRequest(Main.link + "games/hangman/start/spiel/loeschen", "{ 'poolID':'" + Main.poolID  + "' }");
            menue1();
        } else {
            //hier wurde das Spiel gestartet
            //der Pool muss nun im Server geloescht werden
           // Main.posten.doPostRequest(Main.link + "games/hangman/start/spiel/loeschen", "{ 'poolID':'" + Main.poolID  + "' }");
            spiel();
        }


    }

    /**
     * Hier wird der Hauptablauf des Spiels gesteuert und die Ergebnisse des Gegners immer wieder abgefragt bis das Spiel beendet ist.
     * @throws IOException
     * @throws InterruptedException
     */
    public static void spiel() throws IOException, InterruptedException {
        System.err.println("---Spiel gestartet---");

        String antwort = Main.posten.doPostRequest(Main.link+"games/hangman/start/spiel/status", "{ 'poolID':'"+Main.poolID+"','name':'"+Main.name+"' }");
        JsonObject jObj = new Gson().fromJson(antwort, JsonObject.class);

        boolean poolVorhandenB = true;
        boolean spielEnde = false;
        boolean amZug = false;
        int anzahlLeben = 10;
        String text = "Warten auf Gegner ";
        int sekunden = 0;
        String  erraten = jObj.get("erraten").toString();
        erraten = erraten.replace("\"", "");
        String fehlversuche = "";
        String fehlversucheWort = "";

        //Anfrage, wer der Clients anfangen darf zu raten
        antwort = Main.posten.doPostRequest(Main.link+"games/hangman/start/spiel/anfang", "{ 'poolID':'"+Main.poolID+"','name':'"+Main.name+"' }");
        //System.out.println(antwort);

        if (antwort.contains("true")) {  //dieser Nutzer ist dran mit Raten
            amZug = true;
        }

        while(!spielEnde) {
            TimeUnit.SECONDS.sleep(1);
            sekunden = sekunden+1;

            if(amZug){
                System.out.println("Anzahl Leben: "+anzahlLeben);
                System.out.println("Fehlversuche: "+fehlversuche);
                System.out.println("Fehlversuche Wörter: "+fehlversucheWort);
                System.out.println("Erratene Stellen: "+erraten);
                raten();
                amZug = false;  //nach Rateversuch ist Gegner dran
            }else{
                antwort = Main.posten.doPostRequest(Main.link+"games/hangman/start/spiel/status", "{ 'poolID':'"+Main.poolID+"','name':'"+Main.name+"' }");

                //System.out.println(antwort);

                jObj = new  Gson().fromJson(antwort, JsonObject.class);
                String amZugString = jObj.get("amZug").toString();
                amZugString = amZugString.replace("\"", "");

                if(amZugString.contains("true")){
                    amZug = true;
                }else{
                    amZug = false;
                }

                String leben = jObj.get("leben").toString();
                leben = leben.replace("\"", "");

                anzahlLeben = Integer.parseInt(leben);

                String spielVorbei = jObj.get("spielVorbei").toString();
                spielVorbei = spielVorbei.replace("\"", "");

                spielEnde = Boolean.parseBoolean(spielVorbei);

                erraten = jObj.get("erraten").toString();
                erraten = erraten.replace("\"", "");

                fehlversuche = jObj.get("fehlversuche").toString();
                fehlversuche = fehlversuche.replace("\"", "");

                fehlversucheWort = jObj.get("fehlversucheWort").toString();
                fehlversucheWort = fehlversucheWort.replace("\"", "");



                text = text+"*";
                System.out.print(text+" \r");
                if(sekunden%15==0){
                    text =  "Warten auf Gegner ";
                }
            }
        }

        //Anfrage, ob der Client gewonnen hat oder nicht
        //antwort  = Main.posten.doPostRequest(Main.link+"games/hangman/start/spiel/gewonnen","{ 'poolID':'" + Main.poolID + "','name':'"+Main.name+"' }");
        //System.out.println(antwort);


        //der Pool muss nun im Server geloescht werden
        //Nur der ersteller des Pools löscht, damit sich das nicht doppelt
        if(Main.poolErstellt) {
            Main.posten.doPostRequest(Main.link + "games/hangman/start/spiel/loeschen", "{ 'poolID':'" + Main.poolID + "' }");
            Main.poolErstellt = false;
        }
        menue1();

    }


    /**
     * Der Nutzer macht einen Rateversuch. Er hat die Auswahl zwischen Wort und Buchstabe erraten.
     */
    public static void raten() throws IOException, InterruptedException {
        String eingabe = null;
        System.out.println("Bitte geben Sie die 0 ein, wenn Sie einen Buchstaben erraten möchten und eine 1, wenn Sie schon ein ganzes Wort probieren wollen.");

            boolean loop = true;
            int option = -1;

            while(loop){

                eingabe = sc.nextLine();

                if(eingabe.equals("0")){
                    loop = false;
                    option = Integer.parseInt(eingabe);
                } else if (eingabe.equals("1")) {
                    loop = false;
                    option = Integer.parseInt(eingabe);
                }else{
                    System.out.println("Falsche Eingabe");
                }


            }

            if (option == 0) {
                boolean x = true;
                while (x) {  // Falls Eingabe ungültig, wird Eingabe wiederholt
                    System.out.println("Welchen Buchstaben wollen Sie ausprobieren?");
                    eingabe = sc.next();

                    if (eingabe.length() == 1) {
                        char C = eingabe.charAt(0);
                        if (!((C >= 'a' && C <= 'z') || (C >= 'A' && C <= 'Z'))) {   //überprüft,ob Eingabe gültig ist
                            System.out.println("Eingabe nicht korrekt. Bitte geben Sie einen Buchstaben ein!");
                        } else {
                            x = false;
                        }
                    } else {
                        System.out.println("Eingabe zu lang/zu kurz");
                    }
                }
            }
            if (option == 1) {
                boolean x = false;
                while (!x) {
                    System.out.println("Welches Wort wollen Sie ausprobieren?");
                    eingabe = sc.next();

                    x = eingabe.matches("[a-zA-Z]+");  //Überprüft, ob Wort nur Buchstaben enthält

                    if (!x) {
                        System.out.println("Bitte nochmal eingeben. Das Wort darf nur Buchstaben enthalten");  //Wenn Wort nicht nur Buchstaben enthält, wird Eingabe wiederholt
                    }

                    eingabe = eingabe.toLowerCase();

                }
            }

            String antwort = Main.posten.doPostRequest(Main.link + "games/hangman/start/neuesWort/" + option, "{ 'name': '" + Main.name + "','pool': '" + Main.poolID + "','zeichen': '" + eingabe + "'}");  //neuen Postrequest mit Eingabe an Server

            JsonObject jObj = new Gson().fromJson(antwort, JsonObject.class);


            String rateVersuch = jObj.get("rateVersuch").toString();
            rateVersuch = rateVersuch.replace("\"", "");

            boolean antwort2 = Boolean.parseBoolean(rateVersuch);
            //System.out.println(antwort2);

                if (antwort2) {
                    System.out.println("Richtig geraten!");
                    if(option == 1){
                        menue1();
                    }
                } else System.out.println("Leider falsch Geraten! :-(");

    }
}

