import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import javax.swing.*;
import java.io.*;

public class Dino extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 800;
    int boardHeight = 500;

    Image dinosaurGif;
    Image dinosaurJumpImg;
    Image dinosaurDeadImg;
    Image cactus1Img;
    Image cactus2Img;
    Image cactus3Img;
    Image gameOverImg;
    Image resetImg;


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
//    game over
    int gameOverWidth = 500;
    int gameOverHeight = 60;
//    reset
    int resetWidth = 60;
    int resetHeight = 60;
//    physics
    int velocityY = 0;
    int velocityX = -10;
    int gravity = 1;

//    game loop
    Timer gameLoop;
    Timer placedCactusTimer;
    boolean gameOver = false;
    int score = 0;

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
        gameOverImg = new ImageIcon(getClass().getResource("./img/game-over.png")).getImage();
        resetImg = new ImageIcon(getClass().getResource("./img/reset.png")).getImage();

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
        if(gameOver) {
            return;
        }
        double spawnChance = Math.random();
        if(spawnChance > .99) {
            Block cactus = new Block(cactusX, cactusY, cactus3Width, cactusHeight, cactus3Img);
            cactusArr.add(cactus);
        } else if(spawnChance > .60) {
            Block cactus = new Block(cactusX, cactusY, cactus2Width, cactusHeight, cactus2Img);
            cactusArr.add(cactus);
        } else if(spawnChance > .40) {
            Block cactus = new Block(cactusX, cactusY, cactus1Width, cactusHeight, cactus1Img);
            cactusArr.add(cactus);
        }
//        getting rid of the off-screen cacti
        if(cactusArr.size() > 5) {
            cactusArr.remove(0);
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
//        dino
        g.drawImage(dinosaur.image, dinosaur.x, dinosaur.y, dinosaur.width, dinosaur.height, null);
//        cactus
        for (Block cactus : cactusArr) {
            g.drawImage(cactus.image, cactus.x, cactus.y, cactus.width, cactus.height, null);
        }
//        score
        g.setColor(Color.darkGray);
        g.setFont(new Font("Futura", Font.PLAIN, 30));
        if(gameOver) {
            g.drawString("Your Score:  " + String.valueOf(score), 290, 250);
            g.drawImage(gameOverImg, 150, 100, gameOverWidth, gameOverHeight, null);
            g.drawImage(resetImg, 370, 300, resetWidth, resetHeight, null);
        }
        else {
            g.drawString("Score: " + String.valueOf(score) + "   High score: " + String.valueOf(getHighScore()), 25, 50);
        }
    }

    public static void saveHighScore(int score) {
        try (PrintWriter writer = new PrintWriter("high-score.txt")) {
            writer.println(score);
        } catch (IOException e) {
            System.err.println("Error saving high score: " + e.getMessage());
        }
    }

    public static int getHighScore() {
        try (BufferedReader reader = new BufferedReader(new FileReader("high-score.txt"))) {
            return Integer.parseInt(reader.readLine().trim());
        } catch (IOException | NumberFormatException e) {
            return 0; // Default score if file is missing or invalid
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
            if(collision(dinosaur, cactus)) {
                dinosaur.image = dinosaurDeadImg;
                gameOver = true;
            }
        }
//        score
        score++;
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
        if(gameOver) {
            placedCactusTimer.stop();
            gameLoop.stop();
        }
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
        if(gameOver) {
            dinosaur.y = dinoY;
            dinosaur.image = dinosaurGif;
            velocityY = 0;
            cactusArr.clear();
            if(score > getHighScore()) {
                saveHighScore(score);
            }
            score = 0;
            gameOver = false;
            gameLoop.start();
            placedCactusTimer.start();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
}

















