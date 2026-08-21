package ch.tbz.m321servicenoah;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class OtherServicePoller {

    private static final Logger logger = LoggerFactory.getLogger(OtherServicePoller.class);

    private final RestClient restClient;

    public OtherServicePoller(@Value("${other-service.url}") String otherServiceUrl) {
        this.restClient = RestClient.create(otherServiceUrl);
    }

    @Scheduled(fixedDelay = 10_000)
    public void pollOtherService() {
        try {
            String response = restClient.get()
                    .uri("/hello")
                    .retrieve()
                    .body(String.class);

            logger.info("Response from other service: {}", response);
        } catch (RestClientException exception) {
            logger.warn("Other service currently unavailable.");
        }
    }
}
