package ro.cloudavenue.jobscout.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JobCollector {

    private static final Logger log = LoggerFactory.getLogger(JobCollector.class);

    private final List<JobFeed> feeds;
    private final JobService jobService;

    public JobCollector(List<JobFeed> feeds, JobService jobService) {
        this.feeds = feeds;
        this.jobService = jobService;
    }

    @Scheduled(cron = "${jobscout.collector.cron}")
    public void collect() {
        for (JobFeed feed : feeds) {
            String feedName = feed.getClass().getSimpleName();

            try {
                List<JobPosting> postings = feed.fetch();
                int saved = 0;

                for (JobPosting posting : postings) {
                    try {
                        if (jobService.submit(posting.description(), posting.url(), feed.source()).isPresent()) {
                            saved++;
                        }
                    } catch (RuntimeException e) {
                        log.warn("Failed to submit posting from {}", feedName, e);
                    }
                }

                log.info("{}: fetched {} postings, saved {} new", feedName, postings.size(), saved);
            } catch (RuntimeException e) {
                log.warn("Feed {} failed", feedName, e);
            }
        }
    }
}
