package ro.cloudavenue.jobscout.job;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import ro.cloudavenue.jobscout.matching.JobMatchResult;

import java.time.Instant;

@Entity
@Table(name = "job")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content_hash", nullable = false, length = 64)
    @JdbcTypeCode(SqlTypes.CHAR)
    private String contentHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobSource source;

    private String url;

    @Column(nullable = false)
    private String description;

    private Integer score;

    private String reason;

    @Column(name = "matched_at")
    private Instant matchedAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected Job(){}

    public Job(String contentHash, JobSource source, String url, String description){
        this.contentHash = contentHash;
        this.source = source;
        this.url = url;
        this.description = description;
    }

    public void applyMatch(JobMatchResult result) {
        score = result.score();
        reason = result.reason();
        matchedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getContentHash() {
        return contentHash;
    }

    public JobSource getSource() {
        return source;
    }

    public String getUrl() {
        return url;
    }

    public String getDescription() {
        return description;
    }

    public Integer getScore() {
        return score;
    }

    public String getReason() {
        return reason;
    }

    public Instant getMatchedAt() {
        return matchedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
