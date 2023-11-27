import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.Color;
import javax.swing.JPanel;
import java.awt.Stroke;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.StringTokenizer;

public class Leaderboard extends JPanel {

    private int w;
    private int h;

    private ArrayList<Score> lb = new ArrayList<>();
    private Font gameFont = new Font ("Helvetica", 1, 40);
    private Stroke gameStroke = new BasicStroke(14f);
    
    public Leaderboard (int width, int height) {
        this.setPreferredSize(new Dimension(w, h));
        this.setBackground(Color.WHITE);
        this.setDoubleBuffered(true);
        w = width;
        h = height;
    }

    public void setUp() throws IOException {
        BufferedReader r = new BufferedReader(new FileReader("scores.txt"));
        StringTokenizer st;
        String line;
        while ((line = r.readLine()) != null) {
            st = new StringTokenizer(line);
            Score s = new Score(st.nextToken(), Integer.parseInt(st.nextToken()));
            lb.add(s);
        }
        r.close();
        Collections.sort(lb);
        while (lb.size() > 8) lb.remove(0);
        update();
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
        drawX = w/2 - (gameMetrics.stringWidth("Leaderboard"))/2;
        drawY = h/2 - 240 - (gameMetrics.getHeight())/2 + gameMetrics.getAscent();
        g.drawString("Leaderboard", drawX, drawY);

        for (int x = 0; x < lb.size(); ++x) {
            Score s = lb.get(x);
            String name = s.getName();
            int score = s.getScore();
            drawX = w/2 - (gameMetrics.stringWidth("Leaderboard"))/2;
            drawY = h/2 - 240 + (15 + gameMetrics.getAscent()) * (x+2) + gameMetrics.getAscent();
            g.drawString(name, drawX, drawY);
            drawX = w/2 + (gameMetrics.stringWidth("Leaderboard"))/2 - gameMetrics.stringWidth(Integer.toString(score));
            drawY = h/2 - 240 + (15 + gameMetrics.getAscent()) * (x+2) + gameMetrics.getAscent();
            g.drawString(Integer.toString(score), drawX, drawY);
        }
    }
}
