package ch.tbz.m321servicenoah;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class M321ServiceNoahApplication {

    public static void main(String[] args) {
        SpringApplication.run(M321ServiceNoahApplication.class, args);
    }

}
