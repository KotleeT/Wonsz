package wonsz;

import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

/**
 *
 * @author Mateusz
 */
public class Main {
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
    public static void main(String[] args) throws IOException {
        int movements;
        String pausex;
        System.out.print("Ile ruchow weza wygenerowac? ");
        Scanner ile = new Scanner(System.in);
        movements = ile.nextInt();
        String chars = (znaki(movements));
        int read = System.in.read();
        
        new Wonsz(movements, chars);
    }
    
}
