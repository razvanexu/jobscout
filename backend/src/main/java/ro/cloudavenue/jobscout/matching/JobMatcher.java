package ro.cloudavenue.jobscout.matching;

public interface JobMatcher {
    JobMatchResult match(String jobDescription);
}
