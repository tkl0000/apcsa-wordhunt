import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.BasicStroke;
import java.awt.Color;

import javax.swing.JPanel;
import java.awt.Stroke;
import java.awt.event.KeyEvent;
import java.io.FileWriter;
import java.io.IOException;

public class End extends JPanel {

    private int w;
    private int h;

    private int score;
    private String name = "";
    private Font gameFont = new Font ("Helvetica", 1, 40);
    private Stroke gameStroke = new BasicStroke(14f);
    private boolean ended = false;
    
    public End(int width, int height, int s) {
        this.setPreferredSize(new Dimension(w, h));
        this.setBackground(Color.WHITE);
        this.setDoubleBuffered(true);
        score = s;
        w = width;
        h = height;
    }

    public void setUp() throws IOException {
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new keyDispatcher());
        while (!ended) update();
    }

    public void update() {
        repaint();
    }

    public void paint(Graphics g) {

        g.setFont(gameFont);
        FontMetrics gameMetrics = g.getFontMetrics(gameFont);
        Graphics2D g2 = (Graphics2D)g;
        g2.setStroke(gameStroke);

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, w, h);
        g2.setColor(Color.BLACK);
        int drawX, drawY;
        drawX = w/2 - (gameMetrics.stringWidth("Final Score:"))/2;
        drawY = h/2 - 90 - (gameMetrics.getHeight())/2 + gameMetrics.getAscent();
        g2.drawString("Final Score:", drawX, drawY);
        drawX = w/2 - (gameMetrics.stringWidth(Integer.toString(score)))/2;
        drawY = h/2 - 30 - (gameMetrics.getHeight())/2 + gameMetrics.getAscent();
        g2.drawString(Integer.toString(score), drawX, drawY);
        drawX = w/2 - (gameMetrics.stringWidth("Enter Initials:"))/2;
        drawY = h/2 + 30 - (gameMetrics.getHeight())/2 + gameMetrics.getAscent();
        g2.drawString("Enter Initials:", drawX, drawY);
        drawX = w/2 - (gameMetrics.stringWidth(name))/2;
        drawY = h/2 + 90 - (gameMetrics.getHeight())/2 + gameMetrics.getAscent();
        g2.drawString(name, drawX, drawY);
    }

    public void leaderboard() throws IOException {
        FileWriter writer = new FileWriter("scores.txt", true);
        writer.write(name + " " + score + "\n");
        writer.close();
        Leaderboard lb = new Leaderboard(w, h);
        Main.getWindow().remove(this);
        Main.getWindow().add(lb);
        Main.getWindow().pack();
        lb.setUp();
        ended = true;
    }

    private class keyDispatcher implements KeyEventDispatcher {
        @Override
        public boolean dispatchKeyEvent(KeyEvent e) {
            if (e.getID() == KeyEvent.KEY_PRESSED) {
                if (e.getKeyCode() == 8) name = name.substring(0, Math.max(0, name.length()-1));
                else if (e.getKeyCode() == 10) {
                    try {leaderboard();} 
                    catch (IOException e1) {e1.printStackTrace();}
                }
                else if (name.length() != 3 && ((65 <= e.getKeyCode() && e.getKeyCode() <= 90) || (97 <= e.getKeyCode() && e.getKeyCode() <= 122))) name += Character.toUpperCase(e.getKeyChar());
            }
            return false;
        }
    }
}
