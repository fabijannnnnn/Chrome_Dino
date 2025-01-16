import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import javax.swing.*;

public class Dino extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 750;
    int boardHeight = 500;

    Image dinosaurGif;
    Image dinosaurJumpImg;
    Image dinosaurDeadImg;
    Image cactus1Img;
    Image cactus2Img;
    Image cactus3Img;


    class Block{
        int x, y, width, height;
        Image image;

        Block(int x, int y, int width, int height, Image image){
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.image = image;
        }
    }

//    dinosaur
    int dinoWidth = 88;
    int dinoHeight = 94;
    int dinoX = 50;
    int dinoY = boardHeight - dinoHeight;
    Block dinosaur;

//    cactus
    int cactus1Width = 34;
    int cactus2Width = 68;
    int cactus3Width = 102;
    int cactusHeight = 69;
    int cactusX = 700;
    int cactusY = boardHeight - cactusHeight;
    ArrayList<Block> cactusArr;

//    physics
    int velocityY = 0;
    int velocityX = -10;
    int gravity = 1;

//    game loop
    Timer gameLoop;
    Timer placedCactusTimer;

    public Dino(){
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.pink);
        setFocusable(true);
        addKeyListener(this);

        dinosaurGif = new ImageIcon(getClass().getResource("./img/dino-run.gif")).getImage();
        dinosaurJumpImg = new ImageIcon(getClass().getResource("./img/dino-jump.png")).getImage();
        dinosaurDeadImg = new ImageIcon(getClass().getResource("./img/dino-dead.png")).getImage();
        cactus1Img = new ImageIcon(getClass().getResource("./img/cactus1.png")).getImage();
        cactus2Img = new ImageIcon(getClass().getResource("./img/cactus2.png")).getImage();
        cactus3Img = new ImageIcon(getClass().getResource("./img/cactus3.png")).getImage();

//        dinosaur
        dinosaur = new Block(dinoX, dinoY, dinoWidth, dinoHeight, dinosaurGif);

//        cactus
        cactusArr = new ArrayList<Block>();

//        game loop timer
        gameLoop = new Timer(1000/90,this);
        gameLoop.start();

//        cactus timer
        placedCactusTimer = new Timer(1200, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placeCactus();
            }
        });
        placedCactusTimer.start();
    }

    public void placeCactus() {
        double spawnChance = Math.random();
        if(spawnChance > .99) {
            Block cactus = new Block(cactusX, cactusY, cactus3Width, cactusHeight, cactus3Img);
            cactusArr.add(cactus);
        } else if(spawnChance > .70) {
            Block cactus = new Block(cactusX, cactusY, cactus2Width, cactusHeight, cactus2Img);
            cactusArr.add(cactus);
        } else if(spawnChance > .50) {
            Block cactus = new Block(cactusX, cactusY, cactus1Width, cactusHeight, cactus1Img);
            cactusArr.add(cactus);
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.drawImage(dinosaur.image, dinosaur.x, dinosaur.y, dinosaur.width, dinosaur.height, null);

//        cactus
        for (Block cactus : cactusArr) {
            g.drawImage(cactus.image, cactus.x, cactus.y, cactus.width, cactus.height, null);
        }
    }

    public void move() {
//        dinosaur
        velocityY += gravity;
        dinosaur.y = dinosaur.y + velocityY;
        if(dinosaur.y > dinoY) {
            dinosaur.y = dinoY;
            velocityY = 0;
            dinosaur.image = dinosaurGif;
        }
//       cactus
        for (Block cactus : cactusArr) {
            cactus.x += velocityX;
        }
    }

    boolean collision(Block a, Block b) {
        return a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_SPACE) {
//            System.out.println("jump"); // testovanie medzernika
            if(dinosaur.y == dinoY) {  // jump control - can modify for double jump
                velocityY = -17;
                dinosaur.image = dinosaurJumpImg;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
}

















