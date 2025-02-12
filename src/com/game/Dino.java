package com.game;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import javax.swing.*;
import java.io.*;
import java.util.Objects;

public class Dino extends JPanel implements ActionListener, KeyListener {
    final static int boardWidth = 800;
    final static int boardHeight = 500;
    Image dinosaurGif, dinosaurJumpImg, dinosaurDeadImg, cactus1Img, cactus2Img, cactus3Img, gameOverImg, resetImg;

//    Block made static due to its independence on methods and attributes of the Dino class
    static class Block{
        int m_X, m_Y, m_Width, m_Height;
        Image m_Image;
         Block(int x, int y, int width, int height, Image image){
            m_X = x;
            m_Y = y;
            m_Width = width;
            m_Height = height;
            m_Image = image;
        }
    }

//    dinosaur
    final int dinoWidth = 88;
    final int dinoHeight = 94;
    final int dinoX = 50;
    final int dinoY = boardHeight - dinoHeight;
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
    Timer gameLoop, placedCactusTimer;
    boolean gameOver = false;
    int score = 0;

    public Dino()
    {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.pink);
        setFocusable(true);
        addKeyListener(this);

        dinosaurGif = new ImageIcon(Objects.requireNonNull(getClass().getResource("./img/dino-run.gif"))).getImage();
        dinosaurJumpImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("./img/dino-jump.png"))).getImage();
        dinosaurDeadImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("./img/dino-dead.png"))).getImage();
        cactus1Img = new ImageIcon(Objects.requireNonNull(getClass().getResource("./img/cactus1.png"))).getImage();
        cactus2Img = new ImageIcon(Objects.requireNonNull(getClass().getResource("./img/cactus2.png"))).getImage();
        cactus3Img = new ImageIcon(Objects.requireNonNull(getClass().getResource("./img/cactus3.png"))).getImage();
        gameOverImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("./img/game-over.png"))).getImage();
        resetImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("./img/reset.png"))).getImage();

//        dinosaur
        dinosaur = new Block(dinoX, dinoY, dinoWidth, dinoHeight, dinosaurGif);
//        cactus
        cactusArr = new ArrayList</*Block*/>();
//        game loop - fps
        gameLoop = new Timer(1000/90,this);
        gameLoop.start();
//        cactus timer
        placedCactusTimer = new Timer(1200, e -> placeCactus()); //using a lambda to define a single-method interface like actionlistener
        placedCactusTimer.start();
    }

    public void placeCactus()
    {
        if(gameOver)
            return;
        double spawnChance = Math.random();
        if(spawnChance > .99)
        {
            Block cactus = new Block(cactusX, cactusY, cactus3Width, cactusHeight, cactus3Img);
            cactusArr.add(cactus);
        }
        else if(spawnChance > .60)
        {
            Block cactus = new Block(cactusX, cactusY, cactus2Width, cactusHeight, cactus2Img);
            cactusArr.add(cactus);
        }
        else if(spawnChance > .40)
        {
            Block cactus = new Block(cactusX, cactusY, cactus1Width, cactusHeight, cactus1Img);
            cactusArr.add(cactus);
        }
//        getting rid of the off-screen cacti
        if(cactusArr.size() > 5)
        {
            cactusArr.remove(0);
        }
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g)
    {
//        dino
        g.drawImage(dinosaur.m_Image, dinosaur.m_X, dinosaur.m_Y, dinosaur.m_Width, dinosaur.m_Height, null);
//        cactus
        for (Block cactus : cactusArr)
        {
            g.drawImage(cactus.m_Image, cactus.m_X, cactus.m_Y, cactus.m_Width, cactus.m_Height, null);
        }
//        score
        g.setColor(Color.darkGray);
        g.setFont(new Font("Futura", Font.PLAIN, 30));
        if(gameOver)
        {
            g.drawString("Your Score:  " + score, 290, 250);
            g.drawImage(gameOverImg, 150, 100, gameOverWidth, gameOverHeight, null);
            g.drawImage(resetImg, 370, 300, resetWidth, resetHeight, null);
        }
        else
            g.drawString("Score: " + score + "   High score: " + getHighScore(), 25, 50);
//            possible use of String.valueOf(), but it is handled at runtime automatically
    }

    public static void saveHighScore(int score)
    {
        try (PrintWriter writer = new PrintWriter("high-score.txt"))
        {
            writer.println(score);
        }
        catch (IOException e)
        {
            System.err.println("Error saving high score: " + e.getMessage());
        }
    }

    public static int getHighScore()
    {
        try (BufferedReader reader = new BufferedReader(new FileReader("high-score.txt")))
        {
            return Integer.parseInt(reader.readLine().trim());
        }
        catch (IOException | NumberFormatException e) {
            return 0; // Default score if file is missing or invalid
        }
    }

    public void move()
    {
//        dinosaur
        velocityY += gravity;
        dinosaur.m_Y += velocityY;
        if(dinosaur.m_Y > dinoY)
        {
            dinosaur.m_Y = dinoY;
            velocityY = 0;
            dinosaur.m_Image = dinosaurGif;
        }
//       cactus
        for (Block cactus : cactusArr)
        {
            cactus.m_X += velocityX;
            if(collision(dinosaur, cactus))
            {
                dinosaur.m_Image = dinosaurDeadImg;
                gameOver = true;
            }
        }
//        score
        score++;
    }

    boolean collision(Block a, Block b)
    {
        return a.m_X < b.m_X + b.m_Width &&
                a.m_X + a.m_Width > b.m_X &&
                a.m_Y < b.m_Y + b.m_Height &&
                a.m_Y + a.m_Height > b.m_Y;
    }

    public void jumpDino()
    {
        if(dinosaur.m_Y == dinoY) {  // jump control - can modify for double jump
            velocityY = -17;
            dinosaur.m_Image = dinosaurJumpImg;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        move();
        repaint();
        if(gameOver)
        {
            placedCactusTimer.stop();
            gameLoop.stop();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_SPACE)
        {
            if(!gameOver)
                jumpDino();
            else
                resetGame();
        }
//        resetGame();

        if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
        {
            quitGame();
        }

        if(e.getKeyCode() == KeyEvent.VK_R)
        {
            resetGame();
        }
    }

    public void quitGame()
    {
        gameLoop.stop();
        System.exit(0);
    }

    public void resetGame() {
        dinosaur.m_Y = dinoY;
        dinosaur.m_Image = dinosaurGif;
        velocityY = 0;
        cactusArr.clear();

        if (score > getHighScore())
        {
            saveHighScore(score);
        }

        score = 0;
        gameOver = false;
        gameLoop.start();
        placedCactusTimer.start();
    }


    @Override
    public void keyReleased(KeyEvent e) {}
}
