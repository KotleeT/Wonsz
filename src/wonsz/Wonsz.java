package wonsz;


import java.awt.Color;
import java.awt.Dimension;
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
 * @author Mateusz
 */
public final class Wonsz extends JFrame implements KeyListener, Runnable  {
    
    private final JPanel p = new JPanel();
    
    private final int HEIGHT = 600;
    private final int WIDTH = 600;
    
    //Opcje Wunsza
    private final JButton[] head = new JButton[500];
    private final JButton[] foods = new JButton[2000];
    JLabel jlabel = new JLabel("", JLabel.CENTER);
    JLabel game_over = new JLabel("", JLabel.CENTER);

    private final int x = 600, y = 600 ;
    private int lvl = 1;
    private int dirX = 0, dirY = 0;
    private final int speed = 50;
    private final int time = 200;
    private final int foodsy = 300;
    //Koordynaty
    private final int[] snake_X = new int [200];
    private final int[] snake_Y = new int[200];
    
    //Poruszsanie
    private final Point[] snake_Poz = new Point[300];
    private int score = 0;
    private boolean over = false;
    private final int mv;
    private int m = 0;
    private int mx = 0;
    private String ways;
    

    Thread thread;
    Thread thread2;
    Random rm = new Random();
    Random mr = new Random();
    ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    
    public Wonsz(int movements, String chars){
        super("Wonsz");
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setAlwaysOnTop(true);
        mv = movements;
        ways = chars;
        start();
        
        p.setSize(new Dimension(400, 400));
        p.setLayout(null);
        p.setBackground(Color.DARK_GRAY);
        
        add(p);
        show();
        
        init();
        addKeyListener(this);
        
        thread = new Thread(this);
        thread.start();
        
    }
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
        executor.scheduleAtFixedRate(helloRunnable, 0, time, TimeUnit.MILLISECONDS);
    }
    
    //Reset wszystkiego
    public void reset(){
        init();
        p.removeAll();
        game_over.setOpaque(false);
        thread.stop();
        
        start();
        thread = new Thread(this);
        thread.start();
    }
    //Rośnięcie
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
    
    //Samoczynne poruszanie do przodu
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
            snake_X[0] = 10;
        else if(snake_X[0] == 0)
            snake_X[0] = x - 10;
        else if(snake_Y[0] == y - 10)
            snake_Y[0] = 30;
        else if(snake_Y[0] == 20)
            snake_Y[0] = y - 10;
        
        for(int i = 1; i < foodsy; i++){
            if(head[0].getBounds().intersects(foods[i].getBounds())){
                score += 1;
                jlabel.setText("<html><h3>Punkty: " + score + "&nbsp &nbsp &nbsp Ruchy: " + m + "</h3></html>");
                //System.out.println("Punkty: " + score + " " + i);
                levelUp(i);  
            }
        }
        p.repaint();
        show();
    }
 
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
                break;
        }
    }
        Runnable helloRunnable = new Runnable() {
            public void run() {
                move(ways.charAt(m));
            }
        };
    
    @Override
    public void run() {
        while(!over){
            move_Forward();
            for(int i = 1; i<lvl-1; i++){
                if(head[0].getBounds().intersects(head[i].getBounds()) || m == mv){
                    over = true;
                }
            }
            try{
                Thread.sleep(speed);
            }catch(InterruptedException e){
            }
        } 
      if(over){
          executor.shutdown();
          game_over.setText("<html><span style='font-size: 15px; text-align: center;'>&nbsp Koniec Gry!</span><br/> &nbsp &nbsp &nbsp &nbsp &nbsp &nbsp Ruch: " + m + "<br/><span style='font-size: 10px;'>Naciśnij R aby wyjść<span></html>");
          game_over.setOpaque(true);
          game_over.setBackground(Color.BLACK);
          thread.stop();
      }
    }
    

    @Override
    public void keyTyped(KeyEvent e) {
        
    }

    @Override
    public void keyPressed(KeyEvent e) {
        //Lewo
        if(e.getKeyCode() == 82){
            System.exit(0);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        
    }
    
    
    
}
