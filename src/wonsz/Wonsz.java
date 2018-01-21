package wonsz;


import java.awt.Color;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import java.lang.ClassCastException;
/**
 *
 * @author Mateusz Kotlarczyk, Bartłomiej Leśniak
 * @version 0.9
 * 
 */
public final class Wonsz extends JFrame implements KeyListener, Runnable  {
    
    //Utworzenie JPanel'u - pola gry
    private final JPanel p = new JPanel();
    //Parametry odpowiedzialne za wielkość pola gry
    private final int Height = 600;
    private final int Width = 600;
    
    //Opcje Wunsza
    //Maksymalna długoś węża - 5000
    private final JButton[] head = new JButton[5000];
    //Maksymalna ilość jedzenia - 2000
    private final JButton[] foods = new JButton[2000];
    
    //Utworzenie dwóch osobnych pól wyświetlających tekst.
    JLabel jlabel = new JLabel("", JLabel.CENTER);
    JLabel game_over = new JLabel("", JLabel.CENTER);

    //Pomocnicze zmienne do wielkości
    private final int x = 600, y = 600;
    //Określenie startowego poziomu
    private int lvl = 1;
    //Nadanie domyślnych kierunków poruszania
    private int dirX = 0, dirY = 0;
    //Zmienne odpowiedzialne za szybkość gry, szybkość zmiany kierunków oraz ilość jedzenia na planszy
    private final int speed = 50;
    private final int time = 200;
    private final int foodsy = 400;
    
    //Koordynaty pomocnicze
    private final int[] snake_X = new int [500];
    private final int[] snake_Y = new int[500];
    
    //Zmienna do odczytu pozycji węża
    private final Point[] snake_Poz = new Point[500];
    
    private int score = 0;
    private boolean over = false;
    
    //Zmienne pomocnicze do określania ilości ruchów
    private final int mv;
    private int m = 0;
    private final int mvsx;
    private String ways;  
    
    private final String eatx;
    private boolean kannibal = false;
    
    //Utworzenie nowego wątku - dzięki temu gra działa
    Thread thread;
    //Dwa pomocnicze randomy
    Random rm = new Random();
    Random mr = new Random();
    
    //Wykonywanie funkcji zmiany kierunków w określonych odstępach czasowych - deklaracja
    ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    
    /**
     * Główna metoda programu odpowiedzianla za wywołanie odpowiednich funkcji.
     * Rysuje pole gry, nadaje mu rozmiar oraz odpowiednie opcje.
     * Wczytuje dane podane przez użytkownika i przekazuje do wywoływanych metod.
     * 
     * @param movements - ilość ruchów, które wykona wąż
     * @param chars - wygenerowane wcześniej ruchy węża
     * @param eat - decyzja użytkownika, czy wąż się zje
     * @param mvs - określony ruch, w którym wąż się zje
     */
    public Wonsz(int movements, String chars, String eat, int mvs){
        //Nagłówek programu
        super("Wonsz");
        //Ustawienie wielkości okna
        setSize(Width, Height);
        //Relacja do...
        setLocationRelativeTo(null);
        //Skalowanie okna
        setResizable(false);
        //Domyślna opcja zamknięcia
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //Zawsze na wierzchu
        setAlwaysOnTop(true);
        
        mv = movements;
        ways = chars;
        eatx = eat;
        mvsx = mvs;
        
        start();
        
        p.setLayout(null);
        p.setBackground(Color.DARK_GRAY);
        
        add(p);
        show();
        init();
        
        //Oczekiwanie na klawisze
        addKeyListener(this);
        
        //Wystartowanie wątka gry
        thread = new Thread(this);
        thread.start();    
    }
    /**
     * Funkcja ustawiająca domyślne opcje startu gry
     */
    public void init(){
        lvl = 1;
        m = 0;
        snake_X[0] = 400;
        snake_Y[0] = 300;
        dirX = 0;
        dirY = -10;
        score = 0;
        over = false;
    }
    /**
     * Funkcja w której zostaje dodany pasek informacyjny u góry okna,
     * okno końca gry - ukryte oraz generowana jest głowa węża - poziom 1.
     * Dodatkowo zostaje utworzona określona ilość jedzenia na planszy.
     * 
     * Na końcu funkcji zostaje uruchuomiony executor odpowiedzialny za wywoływanie
     * funkcji zmiany kierunku w określonym odstępie czasowym
     */
    public void start(){
        //Dodanie napisów - ramka u góry z punktami
        jlabel.setBorder(new LineBorder(Color.cyan));
        jlabel.setForeground(Color.CYAN);
        jlabel.setBounds(0, 0, 600, 20);
        jlabel.setOpaque(true);
        jlabel.setBackground(Color.BLACK);
        
        //Utworzenie okna końca gry - chwilowo ukryte
        game_over.setForeground(Color.CYAN);
        game_over.setBounds(100, 200, 400, 100);
        game_over.setText(""); 
        p.add(game_over); 
        
        //Utworzenie głowy węża
        head[0] = new JButton("head" + 0);
        head[0].setEnabled(false);
        head[0].setBounds(200, 200, 10, 10);
        head[0].setBackground(Color.red);
        p.add(head[0]);
        
        foods[0] = new JButton("foods"+0);
        //Utworzenie na mapie w losowych pozycjach jedzenia
        for(int i = 1; i < foodsy; i++){
            int a = 10 + (10 * rm.nextInt(60));
            int b = 10 + (10 * rm.nextInt(60));
            
            if(b < 30){
                b+=50;
            }
            else if(b > Height - 20){
                b-=100;
            }
            
            if(a < 10){
                a+=50;
            }
            else if(a > Width - 20){
                a=+100;
            }
            
            foods[i] = new JButton("foods"+i);
            foods[i].setEnabled(false);
            foods[i].setBounds(a, b, 10, 10);
            foods[i].setBackground(Color.green);
            p.add(foods[i]);
        }
        
        //Aktualizowanie wyświetlaych punktów
        jlabel.setText("<html><h3>Punkty: " + score + "&nbsp &nbsp &nbsp Ruchy: " + m + "</h3></html>");
        p.add(jlabel);
        
        //Wywoływanie funkcji zmiany kierunku w określonych dostępach czasu
        executor.scheduleAtFixedRate(moveRun, 0, time, TimeUnit.MILLISECONDS);
    }
    
    /**
     * Funkcja przywracająca program do stanu początkowego - w wersji 0.9 nieużywana.
     */
    public void reset(){
        init();
        p.removeAll();
        game_over.setOpaque(false);
        thread.stop();
        
        start();
        thread = new Thread(this);
        thread.start();
    }
    /**
     * Funkcja odpowiedzialna za wydłużanie węża wraz ze zjadanym jedzeniem.
     * W momencie zjedzenia danego punktu w jego miejsce zostaje utworzony kolejny
     * na losowej pozycji, a zjedzony punkt zostaje doczepiony do ogonu węża.
     * 
     * @param q - określenie punktu zjedzonego przez węża, by można było go usunąć z planszy 
     */
    public void levelUp(int q){
        //Usunięcie zjedzonego punktu
        p.remove(foods[q]);
        //System.out.println(q);
        //Utworzenie nowego punktu w losowym miejscu
        foods[q] = new JButton();
        foods[q].setEnabled(false);
        foods[q].setBackground(Color.green);
        p.add(foods[q]);
         
        //Ustawienie nowego punktu
        int a = 10 + (10 * rm.nextInt(44));
        int b = 10 + (10 * rm.nextInt(44));
        //Jeżeli punkt nachodzi na panel u góry - przesówa go
        if(b < 30){
            b+=50;
        }
        foods[q].setBounds(a,b,10,10);
        
        //Przyczepienie zdobytego punktu do ogona węża
        head[lvl] = new JButton("head" + 0);
        head[lvl].setEnabled(false);
        head[lvl].setBounds(-10,0, 10, 10);
        head[lvl].setBackground(Color.cyan);
        p.add(head[lvl]);
        lvl++;    
    }
    
    /**
     * Funkcja poruszająca węża przed siebie.
     * Dodatkowo sprawdza, czy ruchy się nie skończyły oraz wychwytuje jedzone punkty.
     */
    public void move_Forward(){
        //Wyłączenie funkcji zmiany kierunku kiedy skończą się ruchy
        if(m == mv){
            executor.shutdown();
        }
        //Przesówanie węża po planszy
        for(int i = 1; i < lvl; i++){
            snake_Poz[i] = head[i-1].getLocation();
        }
        //Poruszanie głową wunsza
        snake_X[0] += dirX;
        snake_Y[0] += dirY;
        head[0].setBounds(snake_X[0], snake_Y[0], 10, 10);
        head[0].setBackground(Color.RED);
        
        for(int i = 1; i < lvl; i++){
            head[i].setLocation(snake_Poz[i]);
        }
        
        //Logika poruszania przy krawędziach
        if(snake_X[0] == x)
            snake_X[0] = -10;
        else if(snake_X[0] == -20)
            snake_X[0] = x - 10;
        else if(snake_Y[0] == y)
            snake_Y[0] = 20;
        else if(snake_Y[0] == 10)
            snake_Y[0] = y - 20;
        
        //Wychwycenie punktu zjedzonego przez węża
        for(int i = 1; i < foodsy; i++){
            if(head[0].getBounds().intersects(foods[i].getBounds())){
                score += 1;
                jlabel.setText("<html><h3>Punkty: " + score + "&nbsp &nbsp &nbsp Ruchy: " + m + "</h3></html>");
                levelUp(i);  
            }
        }
        p.repaint();
        show();
    }
    
    /**
     * Funkcja zmieniająca kierunek poruszania węża na podstawie przesłanej do niej
     * informacji o aktualnie wykonanym ruchu.
     * @param mo - ruch, który w tym momencie ma wykonać wąż.
     */
    public void move(char mo){
        char moves = mo;
        switch(moves){
            case 'L': 
                //System.out.println("l");
                if(dirX == -10 && dirY == 0){
                    dirX = 0;
                    dirY = 10;
                }
                else if(dirX == 10 && dirY == 0){
                    dirX = 0;
                    dirY = -10;
                }
                else if(dirX == 0 && dirY == -10){
                    dirX = -10;
                    dirY = 0;
                }
                else if(dirX == 0 && dirY == 10){
                    dirX = 10;
                    dirY = 0;
                }
                m++;
                jlabel.setText("<html><h3>Punkty: " + score + "&nbsp &nbsp &nbsp Ruchy: " + m + "</h3></html>");
                break;
            case 'R':
                //System.out.println("r");
                if(dirX == -10 && dirY == 0){
                    dirX = 0;
                    dirY = -10;
                }
                else if(dirX == 10 && dirY == 0){
                    dirX = 0;
                    dirY = 10;
                }
                else if(dirX == 0 && dirY == -10){
                    dirX = 10;
                    dirY = 0;
                }
                else if(dirX == 0 && dirY == 10){
                    dirX = -10;
                    dirY = 0;
                }
                m++;
                jlabel.setText("<html><h3>Punkty: " + score + "&nbsp &nbsp &nbsp Ruchy: " + m + "</h3></html>");
                break;
            case 'F':
                m++;
                jlabel.setText("<html><h3>Punkty: " + score + "&nbsp &nbsp &nbsp Ruchy: " + m + "</h3></html>");
                break;
        }
    }
    /**
     * Funkcja wywoływana przez executor co określony odstęp czasu. Wewnątrz wywołuje funkcję odpowiedzialną za zmianę kierunku.
     */
    Runnable moveRun = new Runnable() {
        @Override
        public void run() {
            move(ways.charAt(m));
        }
    };
    
    /**
     * Metoda uruchomienia odpowiadająca za wywoływanie funkcji poruszania do przodu
     * oraz sprawdzanie końca gry.
     * 
     * Metoda sprawdza, czy wąż sam siebie nie zjada. W przypadku końca gry
     * wyświetla informację o wyniku zależną od typów użytkownika.
     */
    @Override
    public void run() {
        while(!over){
            move_Forward();
            for(int i = 1; i<lvl-1; i++){
                if(head[0].getBounds().intersects(head[i].getBounds())){
                    over = true;
                    kannibal = true;
                }
                else if(m == mv){
                    over = true;
                }
            }
            try{
                //Czas z jakim program się odświeża
                Thread.sleep(speed);
            }catch(InterruptedException e){
            }
        } 
        if(over){
            executor.shutdown();
            if(("T".equals(eatx) || "t".equals(eatx)) && mvsx == m && kannibal){
                game_over.setText("<html><center><span style='font-size: 15px'>Koniec Gry!</span><br/>Ruch: " + m + "<br/><span style='font-size: 10px;'><br/>Przewidziałeś to! Wąż się zjadł!<br/>Naciśnij R aby wyjść</span></center></html>");
            }
            else if(("N".equals(eatx) || "n".equals(eatx)) && mvsx == 0 && !kannibal){
                game_over.setText("<html><center><span style='font-size: 15px'>Udało się!</span><br/>Wąż się nie zjadł, a ty to przewidziałeś!<br/>Naciśnij R aby wyjść</center></html>");
            }
            else
                game_over.setText("<html><center><span style='font-size: 12px;'>Niestety, nie przewidziałeś wyniku.</span></center>");
          
          //Rysowanie tła i ustawienie jego koloru na czarny.
          game_over.setOpaque(true);
          game_over.setBackground(Color.BLACK);
          //Zatrzymanie wątku gry
          thread.stop();
      }
    }
    

    @Override
    public void keyTyped(KeyEvent e) {
        
    }

    @Override
    public void keyPressed(KeyEvent e) {
        //Po naciśnięciu R zamyka program
        if(e.getKeyCode() == 82){
            System.exit(0);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        
    }
    
    
    
}
