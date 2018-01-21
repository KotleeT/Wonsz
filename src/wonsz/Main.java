package wonsz;

import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

/**
 * <h1>Nietypowy Snake!</h1>
 * Program Wonsz symuluje zachowanie Snake'a
 * na podstawie wczytanych od użytkownika danych.
 * Wąż porusza się dzięki przypadkowo wygenerowanym ruchom.
 * Program sprawdza, czy użytkownik dobrze wytypował zakończenie gry. 
 * 
 * 
 * @author Mateusz Kotlarczyk, Bartłomiej Leśniak
 * @version 0.9
 */
public class Main {
    /**
     * Metoda generująca losowe ruchy węża na podstawie wczytanej od użytkownika
     * ilości ruchów
     * 
     * @param movements pobrana od użytkownika ilość ruchów
     * @return zwraca ciąg losowo wygenerowanych znaków określających ruch
     */
    private static String znaki(int movements){
        Random rd = new Random();
        String abc = "LRF";
        String test = "";
        char[] arr;
        arr = new char[movements];
        System.out.print("Twoje wygenerowane znaki: ");
        for(int i = 0; i < movements; i++){
            char letter = abc.charAt(rd.nextInt(abc.length()));
            arr[i] = letter;
            test+=letter;
            System.out.print(arr[i] + " ");
        }
        return test;
    }
    /**
     * Głowna metoda programu wczytująca od użytkownika dane oraz sprawdzająca,
     * czy pobrane dane są prawidłowe
     * @param args nie używane
     * @throws IOException używane do zasymulowania oczekiwania na akcję użytkownika
     * 
     */
    public static void main(String[] args) throws IOException {
        
        int movements;
        int mvs = 0;
        String eat;
        System.out.print("Ile ruchow weza wygenerowac? ");
        Scanner ile = new Scanner(System.in);
        movements = ile.nextInt();
        
        //Wywołanie funkcji znaki() z parametrem ilości ruchów
        String chars = (znaki(movements));
        
        System.out.print("\nCzy wąż się zje? T/N ");
        Scanner e = new Scanner(System.in);
        eat = e.nextLine();
        
        if("T".equals(eat) || "t".equals(eat)){
            System.out.print("\nW którym ruchu to nastąpi? ");
            Scanner ile2 = new Scanner(System.in);
            mvs = ile2.nextInt();
            if(mvs >= movements || mvs == 0){
                System.out.print("\nNie możesz podać takiej liczby. Liczba musi być mniejsza od ilości ruchów oraz większa od zera.\nNaciśnij ENTER aby zamknąć program.");
                int read = System.in.read();
                System.exit(0);
            }
        }
        else if("n".equals(eat) || "N".equals(eat)){
            mvs = 0;
        }
        else{
            System.out.println("BŁĄD");
            System.out.println("Naciśnij ENTER aby zamknąć program.");
            int read = System.in.read();
            System.exit(0);
        }
        System.out.println("Naciśnij ENTER aby kontynuować.");
        int read = System.in.read();
        
        //Wywołanie głównego okna gry z przesłaniem ilości ruchów, ciągu wygenerowanych znaków oraz decyzjami użytkownika
        new Wonsz(movements, chars, eat, mvs);
    }
    
}
