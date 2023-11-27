public class Score implements Comparable<Score> {

    private String name;
    private int score;

    public Score(String n, int s) {
        name = n;
        score = s;
    }

    public String getName() {return name;}
    public int getScore() {return score;}

    @Override
    public int compareTo(Score other) {
        return other.score - this.score;
    }

}
