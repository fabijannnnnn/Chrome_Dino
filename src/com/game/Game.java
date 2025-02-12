package com.game;
import javax.swing.*;

public class Game{
    public static void main(String[] args)
    {
        JFrame frame = new JFrame("Chrome Dino");
        frame.setVisible(true);
        frame.setSize(Dino.boardWidth, Dino.boardHeight);
        frame.setLocationRelativeTo(null); // centers the screen
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Dino chromeDino = new Dino();
        frame.add(chromeDino);
        frame.pack();
        chromeDino.requestFocus();
        frame.setVisible(true);
    }
}