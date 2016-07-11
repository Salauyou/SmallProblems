package ru.iitdgroup.lingutil.match;

public final class ScoredMatch {

    public final int start, end;
    public final double score;

    public ScoredMatch(int start, int end, double score) {
        this.start = start;
        this.end = end;
        this.score = score;
    }
    
    @Override
    public String toString() {
        return String.format("(%s…%s, %.2f)", start, end, score);
    }
    
}
