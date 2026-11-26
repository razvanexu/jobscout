package ro.cloudavenue.jobscout;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = "jobscout.profile-path=classpath:test-profile.md")
@Import(PostgresTestConfiguration.class)
class JobScoutApplicationTests {

    @Test
    void contextLoads() {
    }

}
