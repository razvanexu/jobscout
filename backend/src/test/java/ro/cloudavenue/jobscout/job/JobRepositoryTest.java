package ro.cloudavenue.jobscout.job;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import ro.cloudavenue.jobscout.PostgresTestConfiguration;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(PostgresTestConfiguration.class)
class JobRepositoryTest {

    @Autowired
    private JobRepository jobRepository;

    @Test
    void existsByContentHash_shouldReturnTrue_whenJobIsSaved() {
        // Given
        String hash = ContentHasher.hash("Java Developer Intern");
        jobRepository.save(new Job(hash, JobSource.MANUAL, null, "Java Developer Intern"));

        // When
        boolean exists = jobRepository.existsByContentHash(hash);

        // Then
        assertTrue(exists);
    }

    @Test
    void existsByContentHash_shouldReturnFalse_whenHashIsUnknown() {
        // Given
        String hash = ContentHasher.hash("Unknown job");

        // When
        boolean exists = jobRepository.existsByContentHash(hash);

        // Then
        assertFalse(exists);
    }

    @Test
    void save_shouldFail_whenContentHashIsDuplicated() {
        // Given
        String hash = ContentHasher.hash("Java Developer Intern");
        jobRepository.saveAndFlush(new Job(hash, JobSource.MANUAL, null, "Java Developer Intern"));

        // When / Then
        assertThrows(DataIntegrityViolationException.class,
                () -> jobRepository.saveAndFlush(
                        new Job(hash, JobSource.EMAIL, null, "java developer intern")));
    }
}
