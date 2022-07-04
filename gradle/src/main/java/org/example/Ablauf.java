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
    private static void menue1() throws InterruptedException {
        System.out.println("Was möchten Sie tuen?");
        System.out.println("1: Spielpool beitreten");
        System.out.println("2: Spielpool anlegen");
        System.out.println("3. Logout");
        try {
            int option = sc.nextInt();
            if (option == 1) {
                poolBeitreten();
            }
            if (option == 2) {
                poolAnlegen();
            }
            if (option == 3) {
                logout();
            }
            if(option > 3 || option <= 0){
                System.out.println("Sie koennen nur zwischen 1, 2 und 3 waehlen!");
                menue1();
            }
        } catch (InputMismatchException | IOException i) {
            System.out.println("Ooops, ungueltige Eingabe.");
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
    private static void poolAnlegen() throws InterruptedException {
        System.out.println("Sie haben die Wahl, wechen Schwierigkeitsgrad der Pool haben soll: ");
        System.out.println("1: Anfänger");
        System.out.println("2: Clevere");
        System.out.println("3: Profis");
        System.out.println("4: Absolute Überflieger");
        try {
        int level = sc.nextInt();
          if(!(1<=level && level <= 4)){   //gueltiges Level eingegeben?
                 System.out.println("Sie koenne nur Level 1 bis 4 waehlen!");
                 poolAnlegen();
            }
        System.out.println("Was soll die Pool-ID sein?");
            int id = sc.nextInt();

            String antwort = Main.posten.doPostRequest(Main.link+"games/hangman/start/neuerPool/", "{ 'name': '" + Main.name + "','pool': '" + id + "','level': '" + level + "'}");  //neuen Postrequest mit Eingabe an Server
            boolean antwort2 = Boolean.parseBoolean(antwort);

            if (antwort2) {
                System.out.println("Ein Pool wurde erfolgreich angelegt.");
                Main.poolID = id;
                poolWarteRaum();
            } else System.out.println("Leider ist ein Fehler passiert. Probieren Sie eventuell eine andere ID aus.");
            menue1();
        }
        catch (InputMismatchException | IOException m){
            System.out.println("Ooops, falsche Eingabe.");
        }
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

            try {
                System.out.println("Geben Sie die ID Ihres Wunschpools ein oder die -1 für das Hauptmenü");
                int wunschId = sc.nextInt();
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
            catch (IOException | InputMismatchException i){
                System.out.println("Eine Pool-ID besteht nur aus Zahlen!");
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

        System.out.println("---Warte auf zweiten Spieler---");
        //Prüft jede Sekunde, ob ein Gegner dem Pool beigetreten ist, und das Spiel begonnen hat
        int warteDauer = 40;
        while (!spielGestartet && warteDauer != 0) {
            warteDauer--;
            TimeUnit.SECONDS.sleep(1);
            sekunden = sekunden + 1;

            String antwort = Main.posten.doPostRequest(Main.link + "games/hangman/start/pool/warteRaum", "{ 'poolID':" + Main.poolID + " }");
            System.out.println(antwort);
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

        boolean spielEnde = false;
        boolean amZug = false;
        int anzahlLeben = 10;
        String text = "Warten auf Gegner ";
        int sekunden = 0;
        String  erraten = jObj.get("erraten").toString();
        erraten = erraten.replace("\"", "");
        String fehlversuche = "";

        //Anfrage, wer der Clients anfangen darf zu raten
        antwort = Main.posten.doPostRequest(Main.link+"games/hangman/start/spiel/anfang", "{ 'poolID':'"+Main.poolID+"','name':'"+Main.name+"' }");
        System.out.println(antwort);

        if (antwort.contains("true")) {  //dieser Nutzer ist dran mit Raten
            amZug = true;
        }

        while(!spielEnde) {
            TimeUnit.SECONDS.sleep(1);
            sekunden = sekunden+1;

            if(amZug){
                System.out.println("Anzahl Leben: "+anzahlLeben);
                System.out.println("Fehlversuche: "+fehlversuche);
                System.out.println("Erratene Stellen: "+erraten);
                raten();
                amZug = false;  //nach Rateversuch ist Gegner dran
            }else{

                antwort = Main.posten.doPostRequest(Main.link+"games/hangman/start/spiel/status", "{ 'poolID':'"+Main.poolID+"','name':'"+Main.name+"' }");

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
                System.out.println(erraten);

                fehlversuche = jObj.get("fehlversuche").toString();
                fehlversuche = fehlversuche.replace("\"", "");



                text = text+"*";
                System.out.print(text+" \r");
                if(sekunden%15==0){
                    text =  "Warten auf Gegner ";
                }
            }
        }
        //der Pool muss nun im Server geloescht werden
        Main.posten.doPostRequest(Main.link + "games/hangman/start/spiel/loeschen", "{ 'poolID':'" + Main.poolID  + "' }");

    }


    /**
     * Der Nutzer macht einen Rateversuch. Er hat die Auswahl zwischen Wort und Buchstabe erraten.
     */
    public static void raten(){
        String eingabe = null;
        System.out.println("Bitte geben Sie die 0 ein, wenn Sie einen Buchstaben erraten möchten und eine 1, wenn Sie schon ein ganzes Wort probieren wollen.");
        try {
            int option = sc.nextInt();

            if(option >1 || option < 0) {
                System.out.println("Nur Option 1 oder 2 sind gueltige Eingaben!");
                raten();
            }
            if (option == 0) {
                boolean x = true;
                while (x) {  // Falls Eingabe ungültig, wird Eingabe wiederholt
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
                while (!x) {
                    System.out.println("Welches Wort wollen Sie ausprobieren?");
                    eingabe = sc.next();

                    x = eingabe.matches("[a-zA-Z]+");  //Überprüft, ob Wort nur Buchstaben enthält

                    if (!x) {
                        System.out.println("Bitte nochmal eingeben. Das Wort darf nur Buchstaben enthalten");  //Wenn Wort nicht nur Buchstaben enthält, wird Eingabe wiederholt
                    }
                }
            }

            String antwort = Main.posten.doPostRequest(Main.link+"games/hangman/start/neuesWort/" + option, "{ 'name': '" + Main.name + "','pool': '" + Main.poolID + "','zeichen': '" + eingabe + "'}");  //neuen Postrequest mit Eingabe an Server
            antwort = antwort.replace("{", "");
            antwort = antwort.replace("}", "");
            boolean antwort2 = Boolean.parseBoolean(antwort);
            System.out.println(antwort2);
            if (antwort2) {
                System.out.println("Richtig geraten!");
            } else System.out.println("Leider falscher Buchstabe! :-(");
              }
        catch(IOException e){
            System.out.println("Ungueltige Eingabe");
        }

        }
    }

