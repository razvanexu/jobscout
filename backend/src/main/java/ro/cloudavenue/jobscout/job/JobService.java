package ro.cloudavenue.jobscout.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ro.cloudavenue.jobscout.matching.JobMatchResult;
import ro.cloudavenue.jobscout.matching.JobMatcher;

import java.util.Optional;

@Service
public class JobService {

    private static final Logger log = LoggerFactory.getLogger(JobService.class);

    private final JobRepository jobRepository;
    private final JobMatcher jobMatcher;

    public JobService(JobRepository jobRepository, JobMatcher jobMatcher) {
        this.jobRepository = jobRepository;
        this.jobMatcher = jobMatcher;
    }

    public Optional<Job> submit(String description, String url, JobSource source) {
        String hash = ContentHasher.hash(description);

        if (jobRepository.existsByContentHash(hash)) {
            return Optional.empty();
        }

        Job job = jobRepository.save(new Job(hash, source, url, description));

        try {
            JobMatchResult result = jobMatcher.match(description);
            job.applyMatch(result);
            job = jobRepository.save(job);
        } catch (RuntimeException e) {
            log.warn("Matching failed for job {}", job.getId(), e);
        }

        return Optional.of(job);
    }
}