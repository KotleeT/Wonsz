package wonsz;


import java.awt.Color;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
/**
 *
 * @author Mateusz
 */
public class Wonsz extends JFrame implements KeyListener, Runnable  {
    
    private final JPanel p = new JPanel();
    
    private final int HEIGHT = 600;
    private final int WIDTH = 600;
    
    //Opcje Wunsza
    private JButton[] jb = new JButton[200];
    JLabel jlabel = new JLabel("", JLabel.CENTER);
    JLabel game_over = new JLabel("", JLabel.CENTER);

    private int x = 600, y = 600 ;
    private int rt = 1;
    private int dirX = 0, dirY = 0;
    private boolean food = false;
    private final int speed = 50;
    //Koordynaty
    private int[] snake_X = new int [200];
    private int[] snake_Y = new int[200];
    
    //Poruszsanie
    private boolean l = false;
    private boolean r = false;
    private boolean u = false;
    private boolean d = false;
    
    private Point[] snake_Poz = new Point[300];
    private int score = 0;
    private boolean over = false;
    
    
    Thread thread;
    Random rm = new Random();
    
    public Wonsz(){
        super("Wonsz");
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        start();
        
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
        rt =1;
        snake_X[0] = 400;
        snake_Y[0] = 300;
        dirX = 0;
        dirY = 0;
        food = false;
        score = 0;
        over = false;
        l = false;
        r = true;
        u = true;
        d = true;
    }
    
    public void start(){
        //Napisów - ramka u góry z punktami
        jlabel.setBorder(new LineBorder(Color.cyan));
        jlabel.setForeground(Color.CYAN);
        jlabel.setBounds(0, 0, 600, 20);
     
        game_over.setForeground(Color.CYAN);
        game_over.setBounds(100, 200, 400, 100);
        
        //Aktualizowanie wyświetlaych punktów
        jlabel.setText("<html><h3>Punkty: " + score + "</h3></html>");
        p.add(jlabel);
        
        for(int i = 0; i < rt; i++){
            jb[i] = new JButton("jb" + i );
            jb[i].setEnabled(false);
          
            p.add(jb[i]); 
        }
    }
    
    //Reset wszystkiego
    public void reset(){
        init();
        p.removeAll();
        
        thread.stop();
        
        start();
        thread = new Thread(this);
        thread.start();
    }
    //Rośnięcie
    public void levelUp(){
         jb[rt] = new JButton();
         jb[rt].setEnabled(false);
         jb[rt].setBackground(Color.CYAN);
         p.add(jb[rt]);
         
         //Ustawienie nowego punktu
         int x = 10 + (10 * rm.nextInt(44));
         int y = 10 + (10 * rm.nextInt(44));
         
         snake_X[rt] = x;
         snake_Y[rt] = y;
         
         jb[rt].setBounds(x,y,10,10);
         rt++;
         
    }
    
    public void move_Forward(){
        for(int i = 0; i < rt; i++){
            snake_Poz[i] = jb[i].getLocation();
        }
        
        //Poruszanie głową wunsza
        snake_X[0] += dirX;
        snake_Y[0] += dirY;
        jb[0].setBounds(snake_X[0], snake_Y[0], 10, 10);
        jb[0].setBackground(Color.RED);
        
        for(int i = 1; i < rt; i++){
            jb[i].setLocation(snake_Poz[i - 1]);
        }
        
        //Poruszanie Wunszem
        if(snake_X[0] == x)
            snake_X[0] = 10;
        else if(snake_X[0] == 0)
            snake_X[0] = x - 10;
        else if(snake_Y[0] == y)
            snake_Y[0] = 10;
        else if(snake_Y[0] == 20)
            snake_Y[0] = y - 10;
        
        if(snake_X[0] == snake_X[rt - 1] && snake_Y[0] == snake_Y[rt - 1] && rt > 1){
            food = false;
            score += 1;
            jlabel.setText("<html><h3>Punkty: " + score + "</h3></html>");
            System.out.println("Punkty: " + score);
        }
        if(!food){
            levelUp();
            food = true;
        }
        else{
            jb[rt - 1].setBounds(snake_X[rt - 1], snake_Y[rt - 1], 10, 10);
        }

        p.repaint();
        show();
    }
    
    @Override
    public void run() {
      while(!over){
          for(int i = 1; i<rt; i++){
              if(jb[0].getBounds().intersects(jb[i].getBounds())){
                  
                  over = true;
              }
          }
          move_Forward();
         try{
            Thread.sleep(speed);
         }catch(InterruptedException e){
            e.printStackTrace();
       }
      }
      if(over){
          p.add(game_over);
          game_over.setText("<html><span style='font-size: 15px; text-align: center;'>&nbsp &nbsp &nbsp Koniec Gry!</span><br/><span style='font-size: 10px;'>Naciśnij R żeby zrestartować<span></html>");
          thread.stop();
      }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        
    }

    @Override
    public void keyPressed(KeyEvent e) {
        //Lewo
        if(l == true && e.getKeyCode() == 37){
            dirX = -10;
            dirY = 0;
            r = false;
            u = true;
            d = true;
            System.out.println("Lewo!");
        }
        //Góra
        if(u == true && e.getKeyCode() == 38){
            dirX = 0;
            dirY = -10;
            r = true;
            l = true;
            d = false;
            System.out.println("Góra!");
        }
        if(r == true && e.getKeyCode() == 39){
            dirX = 10;
            dirY = 0;
            l = false;
            u = true;
            d = true;
            System.out.println("Prawo!");
            
        }
        if(d == true && e.getKeyCode() == 40){
            dirX = 0;
            dirY = 10;
            r = true;
            u = false;
            l = true;
            System.out.println("Dół!");
        }
        if(over && e.getKeyCode() == 82){
            reset();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        
    }
    
    
    
}
