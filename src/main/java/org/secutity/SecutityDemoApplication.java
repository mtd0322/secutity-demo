package org.secutity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class SecutityDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecutityDemoApplication.class, args);
    }

}
