package ro.cloudavenue.jobscout.matching;

public record JobMatchResult(int score, String reason) {

    public JobMatchResult {
        if (score < 0 || score > 100) {
            throw new IllegalArgumentException("Score must be 0 -> 100, got: " + score);
        }
    }
}
