package org.example;

import java.io.IOException;
import java.util.Scanner;

public class Ablauf {
    static Scanner sc = new Scanner(System.in);




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

     String antwort = Main.posten.doPostRequest("http://localhost:4567/games/hangman/start/neuesWort/"+ option, "{ "+ eingabe + " }");  //neuen Postrequest mit Eingabe an Server
     System.out.println(antwort);

 }
 }

