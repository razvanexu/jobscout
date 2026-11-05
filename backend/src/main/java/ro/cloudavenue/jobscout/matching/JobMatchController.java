package ro.cloudavenue.jobscout.matching;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/match")
public class JobMatchController {
    private final JobMatcher jobMatcher;

    public JobMatchController(JobMatcher jobMatcher) {
        this.jobMatcher = jobMatcher;
    }

    record MatchRequest(String jobDescription){

    }

    @PostMapping
    public JobMatchResult match(@RequestBody MatchRequest matchRequest){
        return jobMatcher.match(matchRequest.jobDescription());
    }
}
