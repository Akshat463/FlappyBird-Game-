import java.awt.*;
import java.awt.event.*;
import java.nio.channels.Pipe;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import javax.swing.*;
public class FlappyBird extends JPanel implements ActionListener, KeyListener{
    int boardWidth = 360;
    int boardHeight=640;

    Image backGround;
    Image birdImg;
    Image topPipe;
    Image bottomPipe;

    int birdX=boardWidth/8;
    int birdY=boardHeight/2;
    int birdWidth=34;
    int birdHeight=24;

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_NUMPAD5){
            velocityY=-9;
        }
        if(gameOver){
            // restart the game
            bird.y=birdY;
            velocityY=0;
            score=0;
            pipes.clear();
            gameOver=false;
            gameLoop.start();
            placePipesTimer.start();
        }
    }
    @Override
    public void keyReleased(KeyEvent e) {}


    class Bird{

        int x=birdX;
        int y=birdY;
        int width=birdWidth;
        int height=birdHeight;
        Image img;

        Bird(Image img){
            this.img=img;
        }
    }
    //pipes
    int pipeX=boardWidth;
    int pipeY=0;
    int pipeWidth=64;
    int pipeHeight=512;

    class Pipe{
        int x=pipeX;
        int y=pipeY;
        int width=pipeWidth;
        int height=pipeHeight;
        Image img;
        boolean passed=false;

        public Pipe(Image img) {
            this.img = img;
        }
    }

    //Bird Logic
    Bird bird;
    int velocityX=-4;
    int velocityY=0;
    int gravity=1;

    ArrayList<Pipe> pipes;
    Random random=new Random();

    //game loop
    Timer gameLoop;
    Timer placePipesTimer;
    boolean gameOver= false;
    double score=0;

    FlappyBird(){
        setPreferredSize(new Dimension(boardWidth,boardHeight));
        setFocusable(true);
        addKeyListener(this);

        backGround=new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdImg=new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        topPipe=new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottomPipe=new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();

        bird =new Bird(birdImg);
        pipes= new ArrayList<Pipe>();

        placePipesTimer=new Timer(1500,new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                placepipes();
            }
        });
        placePipesTimer.start();

        //  game timer
        gameLoop=new Timer(1000/60,this);
        gameLoop.start();
    }
    public void placepipes(){
        int randomPipeY= (int) (pipeY -  (double) pipeHeight /4 - Math.random()*((double) pipeHeight /2));
        int openSpace=boardHeight/4;
        Pipe toppipe=new Pipe(topPipe);
        toppipe.y=randomPipeY;
        pipes.add(toppipe);

        Pipe bottompipe=new Pipe(bottomPipe);
        bottompipe.y=toppipe.y + pipeHeight + openSpace;
        pipes.add(bottompipe);
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }
    public void draw(Graphics g){

        //Background
        g.drawImage(backGround,0,0,boardWidth,boardHeight,null);

        //bird
        g.drawImage(bird.img,bird.x,bird.y,bird.width,bird.height,null);

        //pipes
        for (Pipe pipe : pipes) {
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        // score
        g.setColor(Color.black);
        g.setFont(new Font("Arial",Font.PLAIN,32));
        if(gameOver){
            g.drawString("Game Over: " + String.valueOf((int)score),10,35);
        }else{
            g.drawString(String.valueOf((int)score),10,35);
        }
    }

    public void move(){

        //bird
        velocityY += gravity;
        bird.y +=velocityY;
        bird.y=Math.max(bird.y,0);

        //pipe
        for(int i=0;i<pipes.size();i++){
            Pipe pipe=pipes.get(i);
            pipe.x += velocityX;

            if(!pipe.passed && bird.x > pipe.x + pipe.width) {
                pipe.passed=true;
                score += 0.5;
            }

            if(collision(bird,pipe)) gameOver=true;
        }

        if(bird.y > boardHeight){
            gameOver=true;
        }
    }

    public boolean collision(Bird a, Pipe b){
        return a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if(gameOver){
            placePipesTimer.stop();
            gameLoop.stop();
        }
    }


}
