package ch.tbz.m321servicenoah;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class OpenApiPartnerServiceClient implements PartnerServiceClient {
    private final RestClient restClient;
    private final String helloPath;

    public OpenApiPartnerServiceClient(@Value("${other-service.url}") String otherServiceUrl,
            @Value("${other-service.hello-path:/api/hello}") String helloPath) {
        this.restClient = RestClient.create(otherServiceUrl);
        this.helloPath = helloPath;
    }

    @Override
    public String getHello() {
        return restClient.get().uri(helloPath).retrieve().body(String.class);
    }
}
