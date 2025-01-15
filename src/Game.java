import javax.swing.*;

public class Game{
    public static void main(String[] args) throws Exception{

        int boardWidth = 750;
        int boardHeight = 500;

        JFrame frame = new JFrame("Chrome Dino");
        frame.setVisible(true);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Dino chromeDino = new Dino();
        frame.add(chromeDino);
        frame.pack();
        chromeDino.requestFocus();
        frame.setVisible(true);
    }
}