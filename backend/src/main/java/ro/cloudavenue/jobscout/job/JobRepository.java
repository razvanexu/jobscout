package ro.cloudavenue.jobscout.job;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, Long> {
    boolean existsByContentHash(String contentHash);
}
