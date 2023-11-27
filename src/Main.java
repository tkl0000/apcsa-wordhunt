import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;
import javax.swing.JFrame;

public class Main {

    private static Game game;
    private static JFrame window;
    public static void main(String[] args) throws IOException {

        int width = 580;
        int height = 700;

        window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("Word Hunt");
        window.setLocationRelativeTo(null);
        window.setVisible(true);
        window.setLayout(new BorderLayout());
        window.setPreferredSize(new Dimension(width, height));

        game = new Game(width, height);
        window.add(game);
        window.pack();
        
        game.setUp();
    }

    public static JFrame getWindow(){return window;}
}
