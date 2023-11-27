import java.awt.Rectangle;

public class Tile extends Rectangle {


    private int boardX, boardY;
    private char[] vowels = new char[]{'a', 'e', 'i', 'o', 'u'};
    private char[] consonants = new char[]{'b', 'c', 'd', 'f', 'g', 'h', 'k', 'l', 'm', 'n', 'p', 'r', 's', 't', 'v', 'y'};
    private char letter;

    public Tile(int x, int y, int size, int bX, int bY) {
        super(x, y, size, size);
        if (Math.random() > 0.5) letter = vowels[(int)(Math.random() * vowels.length)];
        else letter = consonants[(int)(Math.random() * consonants.length)];
        letter = Character.toUpperCase(letter);
        boardX = bX;
        boardY = bY;
    }

    public Tile(int x, int y, int size, char L, int bX, int bY) {
        super(x, y, size, size);
        letter = L;
        boardX = bX;
        boardY = bY;
    }

    public boolean equals(Tile other) {
        return (this.x == other.x && this.y == other.y);
    }

    public char getLetter() {
        return letter;
    }

    public boolean checkAdjacency(Tile other) {
        return ((Math.abs(other.boardX - this.boardX) <= 1) && (Math.abs(other.boardY - this.boardY) <= 1));
    }

    public int getBoardX() {return boardX;}
    public int getBoardY() {return boardY;}

}
