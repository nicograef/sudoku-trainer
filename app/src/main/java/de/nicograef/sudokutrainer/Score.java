package de.nicograef.sudokutrainer;

public class Score implements Comparable<Score> {
    private int totalSeconds;
    private int seconds;
    private int minutes;

    public Score(int totalSeconds) {
        this.totalSeconds = totalSeconds;
        this.seconds = totalSeconds % 60;
        this.minutes = totalSeconds / 60;
    }

    public int getScore() { return totalSeconds; }

    public String toString() {
        if (totalSeconds == 0) return "";
        if (seconds < 10) { return (minutes + "m 0" + seconds + "s"); }
        return (minutes + "m " + seconds + "s");
    }

    @Override
    public int compareTo(Score s) {
        if (totalSeconds == 0) return 1;
        else if (s.getScore() == 0) return -1;
        else if (totalSeconds < s.getScore()) return -1;
        else if (totalSeconds > s.getScore()) return 1;
        return 0;
    }
}
