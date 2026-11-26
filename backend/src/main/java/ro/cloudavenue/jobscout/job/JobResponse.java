package ro.cloudavenue.jobscout.job;

import java.time.Instant;

public record JobResponse(
        Long id,
        JobSource source,
        String url,
        Integer score,
        String reason,
        Instant matchedAt,
        Instant createdAt
) {
    static JobResponse from(Job job) {
        return new JobResponse(
                job.getId(),
                job.getSource(),
                job.getUrl(),
                job.getScore(),
                job.getReason(),
                job.getMatchedAt(),
                job.getCreatedAt()
        );
    }
}