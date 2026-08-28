package ch.tbz.m321servicenoah;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

@Component
public class OtherServicePoller {

    private static final Logger logger = LoggerFactory.getLogger(OtherServicePoller.class);

    private final PartnerServiceClient partnerServiceClient;

    public OtherServicePoller(PartnerServiceClient partnerServiceClient) {
        this.partnerServiceClient = partnerServiceClient;
    }

    @Scheduled(fixedDelay = 10_000)
    public void pollOtherService() {
        try {
            String response = partnerServiceClient.getHello();

            logger.info("Response from other service: {}", response);
        } catch (RestClientException exception) {
            logger.warn("Partner API call failed; this service keeps running: {}", exception.getMessage());
        }
    }
}
