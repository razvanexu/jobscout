package ro.cloudavenue.jobscout.job;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    record SubmitJobRequest(String description, String url) {
    }

    @PostMapping
    public ResponseEntity<JobResponse> submit(@RequestBody SubmitJobRequest request) {
        if (request.description() == null || request.description().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        return jobService.submit(request.description(), request.url(), JobSource.MANUAL)
                .map(job -> ResponseEntity.status(HttpStatus.CREATED).body(JobResponse.from(job)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.CONFLICT).build());
    }
}