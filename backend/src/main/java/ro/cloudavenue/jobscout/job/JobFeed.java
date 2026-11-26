package ro.cloudavenue.jobscout.job;

import java.util.List;

public interface JobFeed {

    JobSource source();

    List<JobPosting> fetch();
}
