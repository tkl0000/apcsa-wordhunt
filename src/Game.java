import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.BasicStroke;
import java.awt.Color;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

public class Game extends JPanel implements MouseListener, MouseMotionListener {

    private int w;
    private int h;

    private BufferedImage cat;
    private Image resizedCat;
    private int catX;
    private int catY;
    private int catStepX;
    private int catStepY;
    private int catWidth;
    private int catHeight;
    private boolean flyingCat = false;

    private double addedY = 70;
    private double addedTransparency = 0.0;
    private int addedScore = 0;
    private int score = 0;
    private long start;
    private Tile[] tiles = new Tile[16];
    private ArrayList<Tile> hover = new ArrayList<>();;
    private boolean chain = false;
    private boolean ended = false;
    private String word = "";
    private boolean validWord = false;
    private Set<String> dictionary = new HashSet<String>();
    private Set<String> used = new HashSet<String>();
    private Font gameFont = new Font ("Helvetica", 1, 40);
    private Stroke gameStroke = new BasicStroke(14f);
    
    public Game(int width, int height) {
        this.setPreferredSize(new Dimension(width, height));
        this.setBackground(Color.WHITE);
        this.setDoubleBuffered(true);
        w = width;
        h = height;
    }

    public void setUp() throws IOException {

        for (int y = 0; y < 4; ++y) {
            for (int x = 0; x < 4; ++x) {
                tiles[4*y+x] = new Tile(60 + 120*x, 160 + 120*y, 100, x, y);
            }
        }

        addMouseListener(this);
        addMouseMotionListener(this);
        start = System.currentTimeMillis();
        
        cat = ImageIO.read(new File("nyan.png"));
        resizedCat = cat.getScaledInstance(150, 50, Image.SCALE_DEFAULT);

        catX = 0-resizedCat.getWidth(null);
        catY = h-resizedCat.getHeight(null);
        catStepX = 1;
        catStepY = 1;
        catWidth = resizedCat.getWidth(null);
        catHeight = resizedCat.getHeight(null);

        BufferedReader r = new BufferedReader(new FileReader("dictionary.txt"));
        String line;
        while ((line = r.readLine()) != null) if (line.length() > 2) dictionary.add(line.toUpperCase());
        r.close();
        while (!ended) update();
        End end = new End(w, h, score);
        Main.getWindow().add(end);
        Main.getWindow().pack();
        end.setUp();
    }

    public void update() {
        checkWord();
        repaint();
    }

    public void paint(Graphics g) {

        g.setFont(gameFont);
        FontMetrics gameMetrics = g.getFontMetrics(gameFont);
        Graphics2D g2 = (Graphics2D)g;
        g2.setStroke(gameStroke);

        //draws base tile
        for (Tile t : tiles) {
            g.setColor(Color.BLACK);
            g.fillRect(t.x, t.y, t.width, t.height);  
            g2.setColor(Color.WHITE);
            int drawX = t.x + (t.width - gameMetrics.stringWidth(Character.toString(t.getLetter())))/2;
            int drawY = t.y + (t.height - gameMetrics.getHeight())/2 + gameMetrics.getAscent();
            g2.drawString(Character.toString(t.getLetter()), drawX, drawY);
        }

        //draw connecting lines
        for (int x = 0; x < hover.size()-1; ++x) {
            g2.setColor(Color.BLACK);
            Tile a = hover.get(x);
            Tile b = hover.get(x+1);
            g2.drawLine(a.x + a.width/2, a.y + a.height/2, b.x + b.width/2, b.y + b.height/2);
        }

        //when hovered draws highlighted tile
        for (Tile h : hover) {
            if (used.contains(word)) g.setColor(Color.YELLOW);
            else if (validWord) g.setColor(Color.GREEN);
            else g.setColor(Color.WHITE);
            g.fillRect(h.x, h.y, h.width, h.height);
            g2.setColor(Color.BLACK);
            int drawX = h.x + (h.width - gameMetrics.stringWidth(Character.toString(h.getLetter())))/2;
            int drawY = h.y + (h.height - gameMetrics.getHeight())/2 + gameMetrics.getAscent();
            g2.drawString(Character.toString(h.getLetter()), drawX, drawY);
        }

        //draw timer
        g.setColor(Color.BLACK);
        String timer = getTime();
        if (timer.equals("0:00")) ended = true;
        g2.drawString(timer, 520 - gameMetrics.stringWidth(timer), 120);

        //draw score
        g2.drawString(Integer.toString(score), 60, 120);

        //draw word
        if (word.length() > 0) g.fillRect((w-gameMetrics.stringWidth(word))/2-10, 120-gameMetrics.getHeight(), gameMetrics.stringWidth(word)+20, gameMetrics.getHeight()+10);
        if (used.contains(word)) g.setColor(Color.YELLOW);
        else if (validWord) g.setColor(Color.GREEN);
        else g.setColor(Color.WHITE);
        g2.drawString(word, (w - gameMetrics.stringWidth(word))/2, 120);

        //draw added score
        if (addedY > 0) addedY -= 0.55;
        if (addedTransparency > 0) addedTransparency -= 0.007;
        g.setColor(new Color(0, 0, 0, (float) addedTransparency));
        g2.drawString("+" + Integer.toString(addedScore), 60, (int) addedY);

        //draw cat
        if (flyingCat) {
            g.drawImage(resizedCat, catX, catY, null);
            catX += catStepX;
            catY += catStepY;
            if (((catX < 0-catWidth || catX > w) && (catY < 0 || catY > h+catHeight))) flyingCat = false;
        }
    }

    public String getTime() {
        int differenceSeconds = 92 - (int)(System.currentTimeMillis() - start)/1000;
        // int differenceSeconds = 91 - (int)(System.currentTimeMillis() - start)/1000;
        int minuteDigit = differenceSeconds / 60;
        int secondsDigit = differenceSeconds % 60;
        String ret = minuteDigit + ":";
        if (secondsDigit < 10) ret += "0";
        ret += secondsDigit;
        return ret;
    }

    public void checkWord() {
        if (dictionary.contains(word)) validWord = true;
        else validWord = false;
    }

    public void cat() {
        if (Math.random() < 0.5) {
            if (Math.random() < 0.5) {
                catX = w-catWidth;
                catY = (int) (h * Math.random());
            }
            else {
                catX = w-catWidth;
                catY = (int) (h * Math.random());
            }
        }
        else {
            if (Math.random() < 0.5) {
                catX = (int) (w * Math.random());
                catY = h-catHeight;
            }
            else {
                catX = (int) (w * Math.random());
                catY = h-catHeight;
            }
        }

        catStepX = ((int) Math.signum(w/2 - catX)) * (1 + (int)(Math.random() * 3))/2;
        catStepY = ((int) Math.signum(h/2 - catY)) * (1 + (int)(Math.random() * 3))/2;

        flyingCat = true;
    }

    public void mouseMoved(MouseEvent e) {
        for (Tile t : tiles) {
            if (e.getX() > t.x && e.getX() < t.x + t.width && e.getY() > t.y && e.getY() < t.y + t.height) {
                Tile hovering = new Tile(t.x + 10, t.y + 10, 80, t.getLetter(), t.getBoardX(), t.getBoardY());
                if ((chain == true || hover.size() == 0) && !hover.contains(hovering)) {
                    hover.add(hovering);
                    word += hovering.getLetter();
                }
                return;
            }
        }
        hover.clear();
        word = "";
    }

    public void mousePressed(MouseEvent e) {
        chain = true;
    }

    public void mouseReleased(MouseEvent e) {
        if (ended) return;
        if (validWord && !used.contains(word)) {
            if (word.length() == 3) addedScore = 100;
            else if (word.length() == 4) addedScore = 400;
            else if (word.length() == 5) addedScore = 800;
            else if (word.length() == 6) addedScore = 1400;
            else if (word.length() == 7) addedScore = 1800;
            else addedScore = 2200;
            addedTransparency = 1.0;
            addedY = 70;
            if (((score + addedScore)/1000) > score/1000) cat();
            score += addedScore;
            used.add(word);
        }
        chain = false;
        word = "";
        hover.clear();
    }

    public void mouseEntered(MouseEvent e) {
    }
 
    public void mouseExited(MouseEvent e) {
    }

    public void mouseDragged(MouseEvent e) {
        for (Tile t : tiles) {
            if (e.getX() > t.x && e.getX() < t.x + t.width && e.getY() > t.y && e.getY() < t.y + t.height) {
                Tile hovering = new Tile(t.x + 10, t.y + 10, 80, t.getLetter(), t.getBoardX(), t.getBoardY());
                if (hover.size() == 0 || (chain == true && t.checkAdjacency(hover.get(hover.size()-1))) && !hover.contains(hovering)) {
                    hover.add(hovering);
                    word += hovering.getLetter();
                }
                return;
            }
        }
    }

    public void mouseClicked(MouseEvent e) {
    }
}
