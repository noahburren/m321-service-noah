package ch.tbz.m321servicenoah;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class M321ServiceNoahApplicationTests {
    @Autowired
    private HelloController helloController;

    @Autowired
    private Environment environment;

    @Test
    void contextLoads() {
    }

    @Test
    void helloEndpointMatchesCurrentOpenApiContract() throws Exception {
        String openApi = Files.readString(Path.of("openapi.yaml"));

        assertThat(environment.getProperty("service-api.hello-path")).isEqualTo("/api/hello");
        assertThat(helloController.hello()).isEqualTo("Hello from Service Noah");
        assertThat(openApi).contains("/api/hello:", "text/plain:", "Hello from Service Noah");
    }

}
